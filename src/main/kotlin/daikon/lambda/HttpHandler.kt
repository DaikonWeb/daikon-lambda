package daikon.lambda

import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.google.gson.Gson
import daikon.core.*
import java.io.InputStream
import java.io.OutputStream


abstract class HttpHandler: RequestStreamHandler {
    private val beforeActions = Routing()
    private val routes = Routing()
    private val exceptions = Exceptions()
    private val afterActions = Routing()
    private val basePath = mutableListOf("")

    abstract fun routing()

    override fun handleRequest(input: InputStream, output: OutputStream, context: com.amazonaws.services.lambda.runtime.Context) {
        output.json(execute(input.asString().toLambdaRequest()))
    }

    private fun InputStream.asString() = bufferedReader().use { it.readText() }

    private fun String.toLambdaRequest(): LambdaRequest {
        return Gson().fromJson(this, LambdaRequest::class.java)
    }

    private fun execute(request: Request): Map<String, Any> {
        val response = LambdaResponse()
        routing()
        RoutingHandler(beforeActions, routes, afterActions, NullContext(), exceptions).execute(request, response)
        return response.asMap()
    }

    fun exception(exception: Class<out Throwable>, action: (Request, Response, Context, Throwable) -> Unit)
            = exception(exception, ContextExceptionAction(action))

    fun exception(exception: Class<out Throwable>, action: (Request, Response, Throwable) -> Unit)
            = exception(exception, DummyExceptionAction(action))

    fun exception(exception: Class<out Throwable>, action: ExceptionAction): HttpHandler {
        exceptions.add(ExceptionRoute(exception, action))
        return this
    }

    fun get(path: String, action: (Request, Response) -> Unit) = get(path, DummyRouteAction(action))

    fun get(path: String, action: (Request, Response, Context) -> Unit) = get(path, ContextRouteAction(action))

    fun get(path: String, action: RouteAction) = add(Method.GET, path, action)

    fun post(path: String, action: (Request, Response) -> Unit) = post(path, DummyRouteAction(action))

    fun post(path: String, action: (Request, Response, Context) -> Unit) = post(path, ContextRouteAction(action))

    fun post(path: String, action: RouteAction) = add(Method.POST, path, action)

    fun put(path: String, action: (Request, Response) -> Unit) = put(path, DummyRouteAction(action))

    fun put(path: String, action: (Request, Response, Context) -> Unit) = put(path, ContextRouteAction(action))

    fun put(path: String, action: RouteAction) = add(Method.PUT, path, action)

    fun delete(path: String, action: (Request, Response) -> Unit) = delete(path, DummyRouteAction(action))

    fun delete(path: String, action: (Request, Response, Context) -> Unit) = delete(path, ContextRouteAction(action))

    fun delete(path: String, action: RouteAction) = add(Method.DELETE, path, action)

    fun options(path: String, action: (Request, Response) -> Unit) = options(path, DummyRouteAction(action))

    fun options(path: String, action: (Request, Response, Context) -> Unit) = options(path, ContextRouteAction(action))

    fun options(path: String, action: RouteAction) = add(Method.OPTIONS, path, action)

    fun head(path: String, action: (Request, Response) -> Unit) = head(path, DummyRouteAction(action))

    fun head(path: String, action: (Request, Response, Context) -> Unit) = head(path, ContextRouteAction(action))

    fun head(path: String, action: RouteAction) = add(Method.HEAD, path, action)

    fun any(path: String, action: (Request, Response) -> Unit) = any(path, DummyRouteAction(action))

    fun any(path: String, action: (Request, Response, Context) -> Unit) = any(path, ContextRouteAction(action))

    fun any(path: String, action: RouteAction) = add(Method.ANY, path, action)

    fun before(path: String = "/*", action: (Request, Response) -> Unit) {
        beforeActions.add(
                Route(
                        Method.ANY,
                        joinPaths(path),
                        DummyRouteAction(action)
                )
        )
    }

    fun before(path: String = "/*", action: (Request, Response, Context) -> Unit) {
        beforeActions.add(
                Route(
                        Method.ANY,
                        joinPaths(path),
                        ContextRouteAction(action)
                )
        )
    }

    fun after(path: String = "/*", action: (Request, Response) -> Unit) {
        afterActions.add(
                Route(
                        Method.ANY,
                        joinPaths(path),
                        DummyRouteAction(action)
                )
        )
    }

    fun after(path: String = "/*", action: (Request, Response, Context) -> Unit) {
        afterActions.add(
                Route(
                        Method.ANY,
                        joinPaths(path),
                        ContextRouteAction(action)
                )
        )
    }

    fun path(path: String, nested: HttpHandler.() -> Unit) {
        basePath.add(path)
        nested.invoke(this)
        basePath.removeAt(basePath.size - 1)
    }

    private fun add(method: Method, path: String, action: RouteAction) {
        routes.add(Route(method, joinPaths(path), action))
    }

    private fun joinPaths(path: String) = basePath.joinToString(separator = "") + path
}