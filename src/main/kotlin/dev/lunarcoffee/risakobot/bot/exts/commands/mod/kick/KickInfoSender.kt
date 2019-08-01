package dev.lunarcoffee.risakobot.bot.exts.commands.mod.kick

import dev.lunarcoffee.risakobot.bot.consts.Emoji
import dev.lunarcoffee.risakobot.framework.api.dsl.embed
import dev.lunarcoffee.risakobot.framework.api.extensions.*
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import dev.lunarcoffee.risakobot.framework.core.std.ContentSender
import net.dv8tion.jda.api.entities.User

internal class KickInfoSender(private val user: User, private val reason: String) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        val offenderName = ctx.event.guild.getMember(user)!!.user.asTag
        ctx.sendSuccess("`$offenderName` has been kicked!")

        // Send PM to kicked user with information.
        user.openPrivateChannel().await().send(
            embed {
                title = "${Emoji.HAMMER_AND_WRENCH}  You were kicked!"
                description = """
                    |**Server name**: ${ctx.event.guild.name}
                    |**Kicker**: ${ctx.event.author.asTag}
                    |**Reason**: $reason
                """.trimMargin()
            }
        )
    }
}
