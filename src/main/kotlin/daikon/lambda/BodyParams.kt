package daikon.lambda

class BodyParams(private val body: String) {
    private var params = mutableMapOf<String, String>()
    init {
        parse()
    }

    fun get(key: String): String? {
        return params[key]
    }

    private fun parse() {
        try {
            body.split("&").forEach {
                val (key, value) = it.split("=")
                params[key] = value
            }
        } catch(e:Throwable) {
            params = mutableMapOf()
        }
    }
}