package dev.lunarcoffee.risako.bot.exts.commands.utility.remind

import dev.lunarcoffee.risako.bot.consts.DEFAULT_TIMER
import dev.lunarcoffee.risako.framework.api.extensions.sendSuccess
import dev.lunarcoffee.risako.framework.core.services.reloaders.ReloadFrom
import dev.lunarcoffee.risako.framework.core.services.reloaders.Reloadable
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.GenericEvent
import java.util.*
import kotlin.concurrent.schedule

@ReloadFrom("Reminders0")
internal class ReminderReloader(
    time: Date,
    guildId: String = "",
    channelId: String = "",
    mention: String = "",
    reason: String = ""
) : Reloadable(time) {

//    constructor(time: Date) : this(time, "", "", "", "")

    init {
        data["guildId"] = guildId
        data["channelId"] = channelId
        data["mention"] = mention
        data["reason"] = reason
    }

    override suspend fun schedule(event: GenericEvent) {
        DEFAULT_TIMER.schedule(time) {
            val guildId = data.get<String>("guildId")
            val channelId = data.get<String>("channelId")
            val mention = data.get<String>("mention")
            val reason = data.get<String>("reason")

            try {
                println("$guildId $channelId")
                val channel = event.jda.getGuildById(guildId)!!.getTextChannelById(channelId)!!
                launch { channel.sendSuccess("Hey, $mention! Here's your reminder: `$reason`") }
            } finally {
                launch { finish("Reminders0") }
            }
        }
    }
}
