package dev.lunarcoffee.risako.bot.consts

import com.google.gson.Gson
import dev.lunarcoffee.risako.bot.std.GuildOverrides
import dev.lunarcoffee.risako.bot.std.RisakoConfig
import dev.lunarcoffee.risako.framework.core.DB
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.time.format.DateTimeFormatter
import java.util.*

const val EMBED_COLOR = 0x6DBEC8

const val RISAKO_CONFIG_PATH = "src/main/resources/risako_config.yaml"
val RISAKO_CONFIG = Yaml().loadAs(File(RISAKO_CONFIG_PATH).readText(), RisakoConfig::class.java)!!

val GSON = Gson()
val DEFAULT_TIMER = Timer(true)

val TIME_FORMATTER = DateTimeFormatter.ofPattern("E dd/MM/yyyy 'at' hh:mm a")!!

val GUILD_OVERRIDES = DB.getCollection<GuildOverrides>(ColName.GUILD_OVERRIDES)
