package dev.lunarcoffee.risako.framework.core

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

internal inline fun <T> silence(crossinline f: () -> T): T? {
    return try {
        f()
    } catch (e: Exception) {
        null
    }
}

fun String.trimToDescription() = trimMargin().replace("\n", " ").replace("\\n", "\n")

fun Timer.scheduleNoInline(time: Date, func: suspend TimerTask.() -> Unit) {
    schedule(
        object : TimerTask() {
            override fun run() {
                GlobalScope.launch { func() }
            }
        },
        time
    )
}
