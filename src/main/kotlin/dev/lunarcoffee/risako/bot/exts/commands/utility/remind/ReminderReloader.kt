package dev.lunarcoffee.risako.bot.exts.commands.utility.remind

import dev.lunarcoffee.risako.bot.consts.DEFAULT_TIMER
import dev.lunarcoffee.risako.bot.consts.RCN
import dev.lunarcoffee.risako.framework.api.extensions.sendSuccess
import dev.lunarcoffee.risako.framework.core.scheduleNoInline
import dev.lunarcoffee.risako.framework.core.services.reloaders.ReloadFrom
import dev.lunarcoffee.risako.framework.core.services.reloaders.Reloadable
import net.dv8tion.jda.api.events.GenericEvent
import java.util.*

@ReloadFrom(RCN.REMINDER)
internal class ReminderReloader(
    time: Date,
    private var guildId: String = "",
    private var channelId: String = "",
    private var mention: String = "",
    private var reason: String = ""
) : Reloadable(time) {

    init {
        colName = RCN.REMINDER
    }

    override suspend fun schedule(event: GenericEvent) {
        DEFAULT_TIMER.scheduleNoInline(time) {
            try {
                val channel = event.jda.getGuildById(guildId)!!.getTextChannelById(channelId)!!
                channel.sendSuccess("Hey, $mention! Here's your reminder: `$reason`")
            } finally {
                finish()
            }
        }
    }
}
