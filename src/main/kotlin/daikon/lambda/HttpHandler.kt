package daikon.lambda

import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.google.gson.Gson
import java.io.InputStream
import java.io.OutputStream


abstract class HttpHandler: RequestStreamHandler {

    abstract fun LambdaCall.routing()

    override fun handleRequest(input: InputStream, output: OutputStream, context: com.amazonaws.services.lambda.runtime.Context) {
        val lambdaCall = LambdaCall(lambdaRequest(input.asString()))
        lambdaCall.routing()
        lambdaCall.start()
        output.json(lambdaCall.response.asMap())
    }

    private fun lambdaRequest(value: String) = Gson().fromJson(value, LambdaRequest::class.java)
}