package daikon.lambda

import daikon.core.Method
import daikon.core.Method.POST
import daikon.core.PathParams
import daikon.core.Request
import java.nio.charset.StandardCharsets
import java.util.*

data class LambdaRequest(
    val requestContext: LambdaRequestContext,
    val path: String,
    val httpMethod: String,
    val headers: Map<String, String>,
    val queryStringParameters: Map<String, String>?,
    val body: String?,
    val isBase64Encoded: Boolean = false
) : Request {
    private lateinit var pathParams: PathParams
    private var attributes = mutableMapOf<String, Any>()
        get() {
            if(field == null) field = mutableMapOf()
            return field
        }

    override fun param(name: String): String {
        val queryString = queryStringParameters ?: mutableMapOf()
        return queryString[name] ?: bodyParams(name) ?: pathParams.valueOf(path()).getValue(name)
    }

    private fun bodyParams(name: String): String? {
        if (method() != POST)
            return null

        return BodyParams(body()).get(name)
    }

    override fun header(name: String): String {
        return headers[name]!!
    }

    override fun hasHeader(name: String): Boolean {
        return headers.containsKey(name)
    }

    override fun body(): String {
        if(body == null) {
            return ""
        }

        if(isBase64Encoded) {
            return String(Base64.getDecoder().decode(body), StandardCharsets.UTF_8)
        }

        return body
    }

    override fun url(): String {
        return (requestContext.domainName ?: "") + path
    }

    override fun path(): String {
        return path
    }

    override fun <T> attribute(key: String, value: T) {
        attributes[key] = value as Any
    }

    override fun <T> attribute(key: String): T {
        @Suppress("UNCHECKED_CAST")
        return attributes[key]!! as T
    }

    override fun method(): Method {
        return Method.valueOf(httpMethod)
    }

    override fun withPathParams(value: String): Request {
        pathParams = PathParams(value)
        return this
    }

    data class Elb(val targetGroupArn: String?)

    data class LambdaRequestContext(
            val elb: Elb?,
            val domainName: String?,
            val identity: Map<String, String>
    )
}