package daikon.lambda

import com.google.gson.Gson
import java.io.InputStream
import java.io.OutputStream


fun InputStream.asString() = bufferedReader().use { it.readText() }

fun OutputStream.json(responseMap: Map<String, Any>) = write(Gson().toJson(responseMap).toByteArray())