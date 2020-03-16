package daikon.lambda

import daikon.core.Context

class NullContext : Context {
    override fun addAttribute(key: String, value: Any) {
        TODO("Not yet implemented")
    }

    override fun <T> getAttribute(key: String): T {
        TODO("Not yet implemented")
    }

    override fun port(): Int {
        TODO("Not yet implemented")
    }
}