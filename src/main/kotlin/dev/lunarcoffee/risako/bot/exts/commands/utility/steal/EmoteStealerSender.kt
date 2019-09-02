package dev.lunarcoffee.risako.bot.exts.commands.utility.steal

import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.dsl.embedPaginator
import dev.lunarcoffee.risako.framework.api.extensions.*
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.ContentSender

class EmoteStealerSender(private val limit: Int) : ContentSender {
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
