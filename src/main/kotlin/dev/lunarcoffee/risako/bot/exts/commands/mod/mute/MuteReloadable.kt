package dev.lunarcoffee.risako.bot.exts.commands.mod.mute

import dev.lunarcoffee.risako.bot.consts.ColName
import dev.lunarcoffee.risako.bot.consts.DEFAULT_TIMER
import dev.lunarcoffee.risako.framework.core.scheduleNoInline
import dev.lunarcoffee.risako.framework.core.services.reloaders.*
import net.dv8tion.jda.api.events.GenericEvent
import java.util.*

@ReloadFrom(ColName.MUTE)
internal class MuteReloadable(
    time: Date,
    val guildId: String,
    val channelId: String,
    val userId: String,
    val prevRoleIds: List<String>,
    val mutedRole: String,
    val reason: String
) : Reloadable(time) {

    override suspend fun schedule(event: GenericEvent, col: ReloadableCollection<Reloadable>) {
        DEFAULT_TIMER.scheduleNoInline(time) {

        }
    }
}
