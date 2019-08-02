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

// The extension function [Timer#schedule(Date, TimerTask.() -> Unit)] seems to break when used
// with the reloader system due to the inlining of the lambda argument. This is to alleviate that
// pain, and also add new functionality with a suspend lambda.
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
