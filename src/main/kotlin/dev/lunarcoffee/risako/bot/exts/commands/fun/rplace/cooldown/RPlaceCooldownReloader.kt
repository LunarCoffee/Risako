package dev.lunarcoffee.risako.bot.exts.commands.`fun`.rplace.cooldown

import dev.lunarcoffee.risako.bot.consts.ColName
import dev.lunarcoffee.risako.bot.consts.DEFAULT_TIMER
import dev.lunarcoffee.risako.framework.core.scheduleNoInline
import dev.lunarcoffee.risako.framework.core.services.reloaders.*
import net.dv8tion.jda.api.events.GenericEvent
import java.util.*

@ReloadFrom(ColName.RPLACE_COOLDOWN)
internal class RPlaceCooldownReloader(time: Date, val userId: String) : Reloadable(time) {
    init {
        colName = ColName.RPLACE_COOLDOWN
    }

    override suspend fun schedule(event: GenericEvent, col: ReloadableCollection<Reloadable>) {
        DEFAULT_TIMER.scheduleNoInline(time) { col.deleteOne { it.rjid == rjid } }
    }
}
