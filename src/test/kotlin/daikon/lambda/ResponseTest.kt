package daikon.lambda

import daikon.core.HttpStatus.CREATED_201
import daikon.core.HttpStatus.MOVED_TEMPORARILY_302
import daikon.core.HttpStatus.OK_200
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.OutputStream

class ResponseTest {
    private val output = mockk<OutputStream>(relaxed = true)

    @Test
    fun `status code`() {
        val input = apiGatewayEvent(method = "GET", path = "/")

        runHandler(input, output) {
            any("/") { _, res -> res.status(CREATED_201)  }
        }

        verify { output.json(mapOf(
                "statusCode" to CREATED_201,
                "headers" to emptyMap<String, String>(),
                "body" to ""
        )) }
    }

    @Test
    fun `content type`() {
        val input = apiGatewayEvent(method = "GET", path = "/")

        runHandler(input, output) {
            any("/") { _, res -> res.type("application/json")  }
        }

        verify { output.json(mapOf(
                "statusCode" to OK_200,
                "headers" to mapOf("Content-Type" to "application/json"),
                "body" to ""
        )) }
    }

    @Test
    fun headers() {
        val input = apiGatewayEvent(method = "GET", path = "/")

        runHandler(input, output) {
            any("/") { _, res -> res.header("foo", "bar")  }
        }

        verify { output.json(mapOf(
                "statusCode" to OK_200,
                "headers" to mapOf("foo" to "bar"),
                "body" to ""
        )) }
    }

    @Test
    fun body() {
        var body = ""
        val input = apiGatewayEvent(method = "GET", path = "/")

        runHandler(input, output) {
            before("/") { _, res -> res.write("Hi") }
            any("/") { _, res ->
                res.write(" Bob")
                body = res.body()
            }
        }

        assertThat(body).isEqualTo("Hi Bob")
    }

    @Test
    fun `redirect to absolute path`() {
        val input = apiGatewayEvent(method = "GET", path = "/foo")

        runHandler(input, output) {
            any("/foo") { _, res -> res.redirect("http://localhost:4545/bar") }
        }

        verify { output.json(mapOf(
                "statusCode" to MOVED_TEMPORARILY_302,
                "headers" to mapOf("Location" to "http://localhost:4545/bar"),
                "body" to ""
        )) }
    }
}