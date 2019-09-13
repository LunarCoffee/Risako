package dev.lunarcoffee.risako.framework.core.dispatchers

interface DispatchableArgs {
    val items: List<Any?>

    operator fun <T> get(index: Int): T
}
