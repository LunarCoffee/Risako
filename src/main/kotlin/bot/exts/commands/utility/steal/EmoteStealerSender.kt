package bot.exts.commands.utility.steal

import bot.consts.Emoji
import framework.api.dsl.embed
import framework.api.dsl.embedPaginator
import framework.api.extensions.*
import framework.core.commands.CommandContext
import framework.core.std.ContentSender

internal class EmoteStealerSender(private val limit: Int) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        val pmChannel = ctx.event.author.openPrivateChannel().await()
        pmChannel.sendSuccess("Your emotes are being processed!")

        val emotePages = ctx
            .event
            .channel
            .iterableHistory
            .take(limit)
            .flatMap { it.emotes }
            .distinct()
            .map { "**${it.name}**: [image link](${it.imageUrl})" }
            .chunked(16)
            .map { it.joinToString("\n") }

        if (emotePages.isEmpty()) {
            ctx.sendError("There were no emotes in the last `$limit` messages!")
            return
        }

        pmChannel.send(
            ctx.embedPaginator {
                for (emotePage in emotePages) {
                    page(
                        embed {
                            title = "${Emoji.SPY}  Your stolen emotes:"
                            description = emotePage
                        }
                    )
                }
            }
        )
        ctx.sendSuccess("Your stolen emotes have been sent to you!")
    }
}
