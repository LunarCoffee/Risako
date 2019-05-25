package dev.lunarcoffee.risako.framework.core

internal inline fun <T> silence(crossinline f: () -> T): T? {
    return try {
        f()
    } catch (e: Exception) {
        null
    }
}
