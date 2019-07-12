package bot.exts.commands.mod.ban

import bot.consts.Emoji
import framework.api.dsl.embed
import framework.api.extensions.*
import framework.core.commands.CommandContext
import framework.core.std.ContentSender
import net.dv8tion.jda.api.entities.User

internal class BanInfoSender(
    private val offender: User,
    private val reason: String
) : ContentSender {

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
