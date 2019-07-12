package bot.exts.commands.mod.mutel

import bot.consts.ColName
import bot.consts.Emoji
import bot.exts.commands.mod.unmute.UnmuteReloader
import framework.api.dsl.embed
import framework.api.dsl.embedPaginator
import framework.api.extensions.send
import framework.api.extensions.sendSuccess
import framework.core.commands.CommandContext
import framework.core.services.reloaders.ReloadableCollection
import framework.core.std.ContentSender
import framework.core.std.SplitTime
import java.util.*

internal class MuteListSender : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        val mutedPages = col
            .find { it.guildId == ctx.event.guild.id }
            .associate { it to ctx.event.guild.getMemberById(it.userId)!! }
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
