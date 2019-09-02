package dev.lunarcoffee.risako.bot.exts.commands.mod.ban

import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.extensions.*
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.ContentSender
import net.dv8tion.jda.api.entities.User

class BanInfoSender(private val offender: User, private val reason: String) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.sendSuccess("`${offender.asTag}` has been banned!")

        // Send PM to banned user with information.
        offender.openPrivateChannel().await().send(
            embed {
                title = "${Emoji.HAMMER_AND_WRENCH}  You were banned!"
                description = """
                    |**Server name**: ${ctx.event.guild.name}
                    |**Banner**: ${ctx.event.author.asTag}
                    |**Reason**: $reason
                """.trimMargin()
            }
        )
    }
}
