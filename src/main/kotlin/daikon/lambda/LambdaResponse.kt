package daikon.lambda

import daikon.core.Response
import java.io.StringWriter

class LambdaResponse : Response {
    private var statusCode: Int = 200
    private var headers: MutableMap<String, String> = mutableMapOf()
    private val writer = StringWriter()

    override fun write(text: String) {
        writer.write(text)
    }

    override fun status(code: Int) {
        statusCode = code
    }

    override fun type(contentType: String) {
        header("Content-Type", contentType)
    }

    override fun header(name: String, value: String) {
        headers[name] = value
    }

    override fun body() = writer.toString()

    override fun redirect(path: String, status: Int) {
        status(status)
        header("Location", path)
    }

    fun asMap() = mapOf(
        "statusCode" to statusCode,
        "headers" to headers,
        "body" to body()
    )
}