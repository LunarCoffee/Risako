package dev.lunarcoffee.risako.bot.exts.commands.utility.remind

import dev.lunarcoffee.risako.bot.consts.ColName
import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.dsl.embedPaginator
import dev.lunarcoffee.risako.framework.api.extensions.*
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.services.reloaders.ReloadableCollection
import dev.lunarcoffee.risako.framework.core.std.SplitTime
import java.time.Instant
import java.util.*

class ReminderManager(private val ctx: CommandContext) {
    suspend fun scheduleReminder(time: SplitTime, reason: String) {
        ctx.run {
            scheduleReloadable(
                ColName.REMINDER,
                ReminderReloader(
                    Date.from(Instant.now().plusMillis(time.totalMs)),
                    event.author.asMention,
                    reason,
                    event.guild.id,
                    event.channel.id
                )
            )
        }
    }

    suspend fun sendRemindersEmbed() {
        val list = getReminders()
        if (list.isEmpty()) {
            ctx.sendSuccess("You have no reminders!")
            return
        }

        // Format each reminder like "**#<number>**: `<reason>` on <time>."
        val reminderPages = list.mapIndexed { i, reminder ->
            val time = SplitTime(reminder.time.time - Date().time).localWithoutWeekday()

            "**#${i + 1}**: `${reminder.reason.replace("`", "")}` on $time"
        }.chunked(16).map { it.joinToString("\n") }

        ctx.send(
            embedPaginator(ctx.event.author) {
                for (reminders in reminderPages) {
                    page(
                        embed {
                            title = "${Emoji.ALARM_CLOCK}  Your reminders:"
                            description = reminders
                        }
                    )
                }
            }
        )
    }

    suspend fun cancelReminders(range: IntRange) {
        val rangeIsMoreThanOne = range.count() > 1
        val reminders = getReminders()

        // Check that the reminder range exists.
        if (reminders.size + 1 in range) {
            ctx.sendError(
                if (rangeIsMoreThanOne)
                    "Some of those reminders don't exist!"
                else
                    "A reminder with that number doesn't exist!"
            )
            return
        }

        // Actually delete the reminders.
        for (index in range) {
            col.deleteOne { it.rjid == reminders[index - 1].rjid }
            reminders[index - 1].finish()
        }

        val pluralThat = if (rangeIsMoreThanOne) "those reminders" else "that reminder"
        ctx.sendSuccess("I've removed $pluralThat!")
    }

    private suspend fun getReminders(): List<ReminderReloader> {
        return col.find { it.mention == ctx.event.author.asMention }.sortedBy { it.time }
    }

    companion object {
        private val col = ReloadableCollection(ColName.REMINDER, ReminderReloader::class)
    }
}
