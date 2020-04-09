package daikon.lambda

import daikon.core.DaikonServer
import daikon.core.RoutingHandler

class LambdaCall(private val request: LambdaRequest, val response: LambdaResponse = LambdaResponse()): DaikonServer() {
    override fun start(routingHandler: RoutingHandler): DaikonServer {
        routingHandler.execute(request, response)
        return this
    }

    override fun stop() { }
}