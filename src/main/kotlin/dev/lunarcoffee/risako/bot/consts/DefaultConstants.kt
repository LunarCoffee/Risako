package dev.lunarcoffee.risako.bot.consts

//import dev.lunarcoffee.beanly.exts.commands.utility.BeanlyConfig
//import dev.lunarcoffee.beanly.exts.commands.utility.GuildOverrides
import java.time.format.DateTimeFormatter
import java.util.*

//const val BEANLY_CONFIG_PATH = "src/main/resources/beanly_config.yaml"
//val BEANLY_CONFIG = Yaml().loadAs(File(BEANLY_CONFIG_PATH).readText(), BeanlyConfig::class.java)!!

const val EMBED_COLOR = 0xFFECA8

val DEFAULT_TIMER = Timer(true)

val TIME_FORMATTER = DateTimeFormatter.ofPattern("E dd/MM/yyyy 'at' hh:mm a")!!
//val GSON = Gson()

//val CLIENT = KMongo.createClient().coroutine
//val DB = CLIENT.getDatabase("BeanlyMongoDB")
//val GUILD_OVERRIDES = DB.getCollection<GuildOverrides>("GuildOverrides")

val COL_NAMES = mapOf(
    "MuteTimer" to "MuteTimers0",
    "RemindTimer" to "ReminderTimers0",
    "RPlaceTimer" to "RPlaceCooldownTimers0"
)
