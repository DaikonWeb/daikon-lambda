package daikon.lambda

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BodyParamsTest {

    @Test
    fun `params well formatted`() {
        val body = "parameter=value&also=another"

        val bodyParams = BodyParams(body)

        assertThat(bodyParams.get("parameter")).isEqualTo("value")
        assertThat(bodyParams.get("also")).isEqualTo("another")
    }

    @Test
    fun `wrong format of params`() {
        val body = "parameter&also=another"

        assertThat(BodyParams(body).get("parameter")).isEqualTo(null)
        assertThat(BodyParams(body).get("also")).isEqualTo(null)
    }
}