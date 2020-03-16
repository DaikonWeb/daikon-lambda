package daikon.lambda

import com.google.gson.Gson
import java.io.OutputStream

fun OutputStream.json(response: Map<String, Any>) {
    write(Gson().toJson(response).toByteArray())
}