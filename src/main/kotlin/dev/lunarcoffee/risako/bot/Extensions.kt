package dev.lunarcoffee.risako.bot

fun Boolean.toYesNo() = if (this) "yes" else "no"

fun String.constToEng() = replace("_", " ").toLowerCase()

fun <T : Enum<T>> Enum<T>.constToEng() = name.replace("_", " ").toLowerCase()
