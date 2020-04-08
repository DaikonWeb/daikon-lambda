package daikon.lambda

import daikon.core.HttpStatus.INTERNAL_SERVER_ERROR_500
import daikon.core.HttpStatus.OK_200
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.io.OutputStream

class RequestTest {
    private val output = mockk<OutputStream>(relaxed = true)

    @Test
    fun `query string parameter`() {
        val input = apiGatewayEvent(method = "GET", path = "/", queryParams = mapOf("name" to "Bob"))

        runHandler(input, output) {
            get("/") { req, res -> res.write("hello ${req.param("name")}") }
        }

        verify { output.json(mapOf(
            "statusCode" to OK_200,
            "headers" to emptyMap<String, String>(),
            "body" to "hello Bob"
        )) }
    }

    @Test
    fun `post parameters`() {
        val input = apiGatewayEvent(method = "POST", path = "/", body = "name=Bob")

        runHandler(input, output) {
            post("/") { req, res -> res.write("hello ${req.param("name")}: ${req.body()}") }
        }

        verify { output.json(mapOf(
                "statusCode" to OK_200,
                "headers" to emptyMap<String, String>(),
                "body" to "hello Bob: name=Bob"
        )) }
    }

    @Test
    fun headers() {
        val input = apiGatewayEvent(method = "POST", path = "/", headers = mapOf("name" to "Bob"))

        runHandler(input, output) {
            post("/") { req, res -> res.write("hello ${req.header("name")}") }
        }

        verify { output.json(mapOf(
                "statusCode" to OK_200,
                "headers" to emptyMap<String, String>(),
                "body" to "hello Bob"
        )) }
    }

    @Test
    fun `header not found`() {
        val input = apiGatewayEvent(method = "POST", path = "/")

        runHandler(input, output) {
            exception(Throwable::class.java) { _, res, _ -> res.status(INTERNAL_SERVER_ERROR_500) }
            post("/") { req, res -> res.write("hello ${req.header("name")}") }
        }

        verify { output.json(mapOf(
                "statusCode" to INTERNAL_SERVER_ERROR_500,
                "headers" to emptyMap<String, String>(),
                "body" to ""
        )) }
    }

    @Test
    fun `check if an header is present`() {
        val input = apiGatewayEvent(method = "POST", path = "/", headers = mapOf("first" to "1"))

        runHandler(input, output) {
            post("/") { req, res ->
                res.write("${req.hasHeader("first")} ${req.hasHeader("second")}")
            }
        }

        verify { output.json(mapOf(
                "statusCode" to OK_200,
                "headers" to emptyMap<String, String>(),
                "body" to "true false"
        )) }
    }

    @Test
    fun body() {
        val input = apiGatewayEvent(method = "POST", path = "/", body = "Bob")

        runHandler(input, output) {
            post("/") { req, res -> res.write("Hello ${req.body()}") }
        }

        verify { output.json(mapOf(
                "statusCode" to OK_200,
                "headers" to emptyMap<String, String>(),
                "body" to "Hello Bob"
        )) }
    }

    @Test
    fun `empty body`() {
        val input = apiGatewayEvent(method = "POST", path = "/", body = null)

        runHandler(input, output) {
            post("/") { req, res -> res.write("Foo${req.body()}Bar") }
        }

        verify { output.json(mapOf(
                "statusCode" to OK_200,
                "headers" to emptyMap<String, String>(),
                "body" to "FooBar"
        )) }
    }

    @Test
    fun `path parameter at the end of the path`() {
        val input = apiGatewayEvent(method = "GET", path = "/foo/XL")

        runHandler(input, output) {
            get("/foo/:size") { req, res -> res.write("He wears size ${req.param(":size")}") }
        }

        verify { output.json(mapOf(
                "statusCode" to OK_200,
                "headers" to emptyMap<String, String>(),
                "body" to "He wears size XL"
        )) }
    }

    @Test
    fun `parameters not found`() {
        val input = apiGatewayEvent(method = "GET", path = "/")

        runHandler(input, output) {
            exception(Throwable::class.java) { _, res, _ -> res.status(INTERNAL_SERVER_ERROR_500) }
            get("/") { req, res -> res.write(req.param(":baz")) }
        }

        verify { output.json(mapOf(
                "statusCode" to INTERNAL_SERVER_ERROR_500,
                "headers" to emptyMap<String, String>(),
                "body" to ""
        )) }
    }

    @Test
    fun `request url`() {
        val input = apiGatewayEvent(method = "GET", path = "/123", domain = "localhost:4545")

        runHandler(input, output) {
            get("/:foo") { req, res -> res.write(req.url()) }
        }

        verify { output.json(mapOf(
                "statusCode" to OK_200,
                "headers" to emptyMap<String, String>(),
                "body" to "localhost:4545/123"
        )) }
    }

    @Test
    fun `request url when in elb event`() {
        val input = apiGatewayEvent(method = "GET", path = "/123", domain = null)

        runHandler(input, output) {
            get("/:foo") { req, res -> res.write(req.url()) }
        }

        verify { output.json(mapOf(
                "statusCode" to OK_200,
                "headers" to emptyMap<String, String>(),
                "body" to "/123"
        )) }
    }

    @Test
    fun `request path`() {
        val input = apiGatewayEvent(method = "GET", path = "/123")

        runHandler(input, output) {
            get("/:foo") { req, res -> res.write(req.path()) }
        }

        verify { output.json(mapOf(
                "statusCode" to OK_200,
                "headers" to emptyMap<String, String>(),
                "body" to "/123"
        )) }
    }

    @Test
    fun attribute() {
        val input = apiGatewayEvent(method = "GET", path = "/")

        runHandler(input, output) {
            before("/") { req, _ -> req.attribute("foo_key", "foo_value") }
            get("/") { req, res -> res.write(req.attribute("foo_key")) }
        }

        verify { output.json(mapOf(
                "statusCode" to OK_200,
                "headers" to emptyMap<String, String>(),
                "body" to "foo_value"
        )) }
    }

    @Test
    fun `attribute not found`() {
        val input = apiGatewayEvent(method = "GET", path = "/")

        runHandler(input, output) {
            exception(Throwable::class.java) { _, res, _ -> res.status(INTERNAL_SERVER_ERROR_500) }
            get("/") { req, res ->
                val attribute = req.attribute<String>("any")
                res.write("Hello $attribute")
            }
        }

        verify { output.json(mapOf(
                "statusCode" to INTERNAL_SERVER_ERROR_500,
                "headers" to emptyMap<String, String>(),
                "body" to ""
        )) }
    }

    @Test
    fun method() {
        val input = apiGatewayEvent(method = "POST", path = "/")

        runHandler(input, output) {
            post("/") { req, res -> res.write("${req.method()}") }
        }

        verify { output.json(mapOf(
                "statusCode" to OK_200,
                "headers" to emptyMap<String, String>(),
                "body" to "POST"
        )) }
    }
}
