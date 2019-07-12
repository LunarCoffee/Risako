package bot.exts.commands.mod.mute

import bot.consts.Emoji
import framework.api.dsl.embed
import framework.api.extensions.*
import framework.core.commands.CommandContext
import framework.core.std.ContentSender
import framework.core.std.SplitTime
import net.dv8tion.jda.api.entities.Member

internal class MuteInfoSender(
    private val offender: Member,
    private val time: SplitTime,
    private val reason: String
) : ContentSender {

    override suspend fun send(ctx: CommandContext) {
        val pmChannel = offender.user.openPrivateChannel().await()

        ctx.sendSuccess("`${offender.user.asTag}` has been muted for `$time`!")
        pmChannel.send(
            embed {
                title = "${Emoji.HAMMER_AND_WRENCH}  You were muted!"
                description = """
                    |**Server name**: ${ctx.event.guild.name}
                    |**Muter**: ${ctx.event.author.asTag}
                    |**Time**: $time
                    |**Reason**: $reason
                """.trimMargin()
            }
        )
    }
}
