package dev.lunarcoffee.risakobot.bot.exts.commands.utility.help

import dev.lunarcoffee.risakobot.bot.consts.Emoji
import dev.lunarcoffee.risakobot.framework.api.dsl.embed
import dev.lunarcoffee.risakobot.framework.api.extensions.send
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import dev.lunarcoffee.risakobot.framework.core.std.ContentSender

internal class HelpListSender : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.send(
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

                footer { text = "Type '..help help' for more info." }
            }
        )
    }
}
