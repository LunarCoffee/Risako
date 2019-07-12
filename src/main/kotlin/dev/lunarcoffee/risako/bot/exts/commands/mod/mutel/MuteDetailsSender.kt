package dev.lunarcoffee.risako.bot.exts.commands.mod.mutel

import dev.lunarcoffee.risako.bot.consts.ColName
import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.bot.exts.commands.mod.unmute.UnmuteReloader
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.api.extensions.sendError
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.services.reloaders.ReloadableCollection
import dev.lunarcoffee.risako.framework.core.std.ContentSender
import dev.lunarcoffee.risako.framework.core.std.SplitTime
import net.dv8tion.jda.api.entities.User
import java.util.*

internal class MuteDetailsSender(private val user: User) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        val member = ctx.event.guild.getMember(user)
        if (member == null) {
            ctx.sendError("That user is not a member of this server!")
            return
        }

        val reloadable = col.findOne { it.userId == member.id }
        if (reloadable == null) {
            ctx.sendError("That member isn't muted!")
            return
        }

        val time = SplitTime(reloadable.time.time - Date().time)
        val prevRoles = reloadable
            .prevRoleIds
            .mapNotNull { ctx.event.guild.getRoleById(it)?.asMention }

        ctx.send(
            embed {
                title = "${Emoji.MUTE}  Info on muted member **${member.user.asTag}**:"
                description = """
                    |**Time remaining**: $time
                    |**Previous roles**: $prevRoles
                    |**Reason**: ${reloadable.reason}
                """.trimMargin()
            }
        )
    }

    companion object {
        private val col = ReloadableCollection(ColName.MUTE, UnmuteReloader::class)
    }
}
