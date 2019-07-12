package bot.exts.commands.utility.help

import bot.consts.Emoji
import framework.api.dsl.embed
import framework.api.extensions.send
import framework.core.commands.CommandContext
import framework.core.std.ContentSender

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
