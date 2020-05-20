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

    override fun write(byteArray: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun status(code: Int) {
        statusCode = code
    }

    override fun type(): String {
        return headers["Content-Type"] ?: ""
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

    override fun status() = statusCode

    fun asMap() = mapOf(
        "statusCode" to statusCode,
        "headers" to headers,
        "body" to body()
    )
}