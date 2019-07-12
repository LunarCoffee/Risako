package bot.exts.commands.mod.unmute

import bot.consts.Emoji
import framework.api.dsl.embed
import framework.api.extensions.*
import framework.core.commands.CommandContext
import framework.core.std.ContentSender
import net.dv8tion.jda.api.entities.Member

internal class UnmuteInfoSender(private val offender: Member) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        val pmChannel = offender.user.openPrivateChannel().await()

        ctx.sendSuccess("`${offender.user.asTag}` has been unmuted!")
        pmChannel.send(
            embed {
                title = "${Emoji.HAMMER_AND_WRENCH}  You were manually unmuted!"
                description = """
                    |**Server name**: ${ctx.event.guild.name}
                    |**Unmuter**: ${ctx.event.author.asTag}
                """.trimMargin()
            }
        )
    }
}
