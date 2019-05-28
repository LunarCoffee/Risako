package dev.lunarcoffee.risako.bot.consts

import com.google.gson.Gson
import java.time.format.DateTimeFormatter
import java.util.*

const val EMBED_COLOR = 0x6DBEC8

val GSON = Gson()
val DEFAULT_TIMER = Timer(true)
val TIME_FORMATTER = DateTimeFormatter.ofPattern("E dd/MM/yyyy 'at' hh:mm a")!!
