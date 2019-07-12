package bot.exts.commands.`fun`.rplace.cooldown

import bot.consts.ColName
import bot.consts.DEFAULT_TIMER
import framework.core.scheduleNoInline
import framework.core.services.reloaders.*
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
