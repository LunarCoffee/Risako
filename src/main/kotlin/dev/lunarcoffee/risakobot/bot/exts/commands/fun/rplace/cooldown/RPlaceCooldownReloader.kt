package dev.lunarcoffee.risakobot.bot.exts.commands.`fun`.rplace.cooldown

import dev.lunarcoffee.risakobot.bot.consts.ColName
import dev.lunarcoffee.risakobot.bot.consts.DEFAULT_TIMER
import dev.lunarcoffee.risakobot.framework.core.scheduleNoInline
import dev.lunarcoffee.risakobot.framework.core.services.reloaders.*
import net.dv8tion.jda.api.events.GenericEvent
import java.util.*

@ReloadFrom(ColName.RPLACE_COOLDOWN)
internal class RPlaceCooldownReloader(time: Date, val userId: String) : Reloadable(time) {
    init {
        colName = ColName.RPLACE_COOLDOWN
    }

    override suspend fun schedule(event: GenericEvent, col: ReloadableCollection<Reloadable>) {
        DEFAULT_TIMER.scheduleNoInline(time) { finish() }
    }
}
