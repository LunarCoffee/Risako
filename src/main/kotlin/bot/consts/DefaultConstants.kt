package bot.consts

import com.google.gson.Gson
import bot.std.GuildOverrides
import bot.std.RisakoConfig
import framework.core.DB
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.time.format.DateTimeFormatter
import java.util.*

internal const val EMBED_COLOR = 0x6DBEC8

internal const val RISAKO_CONFIG_PATH = "src/main/resources/risako_config.yaml"
internal val RISAKO_CONFIG = Yaml().loadAs(
    File(RISAKO_CONFIG_PATH).readText(),
    RisakoConfig::class.java
)!!

internal val GSON = Gson()
internal val DEFAULT_TIMER = Timer(true)

internal val TIME_FORMATTER = DateTimeFormatter.ofPattern("E dd/MM/yyyy 'at' hh:mm a")!!

internal val GUILD_OVERRIDES = DB.getCollection<GuildOverrides>(ColName.GUILD_OVERRIDES)
