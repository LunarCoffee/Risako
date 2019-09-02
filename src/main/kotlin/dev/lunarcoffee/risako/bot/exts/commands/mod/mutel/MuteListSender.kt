package dev.lunarcoffee.risako.bot.exts.commands.mod.mutel

import dev.lunarcoffee.risako.bot.consts.ColName
import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.bot.exts.commands.mod.unmute.UnmuteReloader
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.dsl.embedPaginator
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.api.extensions.sendSuccess
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.services.reloaders.ReloadableCollection
import dev.lunarcoffee.risako.framework.core.std.ContentSender
import dev.lunarcoffee.risako.framework.core.std.SplitTime
import java.util.*

class MuteListSender : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        val mutedPages = col
            .find { it.guildId == ctx.event.guild.id }
            .associateWith { ctx.event.guild.getMemberById(it.userId)!! }
            .map { (timer, member) ->
                val time = SplitTime(timer.time.time - Date().time)
                "**${member.user.asTag}**: $time"
            }
            .chunked(16)
            .map { it.joinToString("\n") }

        if (mutedPages.isEmpty()) {
            ctx.sendSuccess("No one in this server is muted!")
            return
        }

        ctx.send(
            embedPaginator(ctx.event.author) {
                for (members in mutedPages) {
                    page(
                        embed {
                            title = "${Emoji.MUTE}  Currently muted members:"
                            description = members
                        }
                    )
                }
            }
        )
    }

    companion object {
        private val col = ReloadableCollection(ColName.MUTE, UnmuteReloader::class)
    }
}
