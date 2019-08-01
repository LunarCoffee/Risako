package dev.lunarcoffee.risakobot.bot.exts.commands.mod.unban

import dev.lunarcoffee.risakobot.bot.consts.Emoji
import dev.lunarcoffee.risakobot.framework.api.dsl.embed
import dev.lunarcoffee.risakobot.framework.api.extensions.*
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import dev.lunarcoffee.risakobot.framework.core.std.ContentSender
import net.dv8tion.jda.api.entities.User

internal class UnbanInfoSender(private val user: User) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.sendSuccess("`${user.asTag}` has been unbanned!")

        // Send PM to unbanned user with information. [user] is actually a fake JDA entity, so we
        // have to get the real representation. This only sends the message when the user is in a
        // guild which the bot is already in, and if they are accepting PMs from those in mutual
        // guilds.
        ctx.jda.getUserById(user.id)?.openPrivateChannel()?.await()?.send(
            embed {
                title = "${Emoji.HAMMER_AND_WRENCH}  You were unbanned!"
                description = """
                    |**Server name**: ${ctx.event.guild.name}
                    |**Unbanner**: ${ctx.event.author.asTag}
                """.trimMargin()
            }
        )
    }
}
