package dev.lunarcoffee.risako.bot.exts.commands.mod.mute

import dev.lunarcoffee.risako.bot.consts.ColName
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.services.reloaders.ReloadableCollection
import dev.lunarcoffee.risako.framework.core.std.SplitTime
import net.dv8tion.jda.api.entities.User

internal class MuteController(private val ctx: CommandContext) {
    suspend fun mute(user: User, time: SplitTime, reason: String) {

    }

    suspend fun unmute(user: User) {

    }

    companion object {
        private val col = ReloadableCollection(ColName.MUTE, MuteReloadable::class)
    }
}
