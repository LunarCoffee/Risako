package dev.lunarcoffee.risako.bot.exts.commands.utility.help

import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.framework.api.dsl.*
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.ContentSender

class HelpListSender : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.send(
            ctx.embedPaginator {
                // Show all commands before each command in each category.
                allCommandsPage(ctx)

                for (group in ctx.bot.commands.map { it.groupName }.distinct()) {
                    // Hide owner category unless the command user is the owner.
                    if (group == "Owner" && ctx.event.author.id != ctx.bot.config.ownerId)
                        continue

                    val commands = ctx.bot.commands.filter { it.groupName == group }
                    page(
                        embed {
                            title = "${Emoji.PAGE_FACING_UP} $group category commands:"
                            description = commands
                                .joinToString("\n") { "**${it.name}**: ${it.description}" }

                            footer { text = "Try '..help help' or '..help <command name>'." }
                        }
                    )
                }
            }
        )
    }

    private fun EmbedPaginatorDsl.allCommandsPage(ctx: CommandContext) {
        return page(
            embed {
                title = "${Emoji.PAGE_FACING_UP}  All commands:"

                for (c in ctx.bot.commands.distinctBy { it.groupName }) {
                    // Hide owner-only commands unless the command user is the owner.
                    if (c.groupName != "Owner" || ctx.event.author.id == ctx.bot.config.ownerId) {
                        val names = ctx
                            .bot
                            .commands
                            .filter { it.groupName == c.groupName }
                            .map { it.name }
                            .sorted()
                        description += "**${c.groupName}**: $names\n"
                    }
                }
                footer { text = "Try '..help help' or '..help <command name>'." }
            }
        )
    }
}
