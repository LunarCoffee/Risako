package bot.exts.commands.utility.remind

import bot.consts.DEFAULT_TIMER
import bot.consts.ColName
import framework.api.extensions.sendSuccess
import framework.core.scheduleNoInline
import framework.core.services.reloaders.*
import net.dv8tion.jda.api.events.GenericEvent
import java.util.*

@ReloadFrom(ColName.REMINDER)
internal class ReminderReloader(
    time: Date,
    val mention: String = "",
    val reason: String = "",
    private val guildId: String = "",
    private val channelId: String = ""
) : Reloadable(time) {

    init {
        colName = ColName.REMINDER
    }

    override suspend fun schedule(event: GenericEvent, col: ReloadableCollection<Reloadable>) {
        DEFAULT_TIMER.scheduleNoInline(time) {
            // Make sure the user hasn't already cancelled the reminder.
            if (!col.contains(this@ReminderReloader)) {
                return@scheduleNoInline
            }

            try {
                val channel = event.jda.getGuildById(guildId)!!.getTextChannelById(channelId)!!
                channel.sendSuccess("Hey, $mention! Here's your reminder: `$reason`")
            } finally {
                finish()
            }
        }
    }
}
