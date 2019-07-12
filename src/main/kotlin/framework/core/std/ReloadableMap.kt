package framework.core.std

import com.google.gson.Gson

internal class ReloadableMap {
    val items = mutableMapOf<String, String>()

    inline operator fun <reified T> get(key: String) = GSON.fromJson(items[key], T::class.java)!!
    operator fun set(key: String, value: Any?) = items.put(key, GSON.toJson(value))

    companion object {
        private val GSON = Gson()
    }
}
