package dev.lunarcoffee.risakobot.bot.exts.commands.mod.mute

import dev.lunarcoffee.risakobot.bot.consts.Emoji
import dev.lunarcoffee.risakobot.framework.api.dsl.embed
import dev.lunarcoffee.risakobot.framework.api.extensions.*
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import dev.lunarcoffee.risakobot.framework.core.std.ContentSender
import dev.lunarcoffee.risakobot.framework.core.std.SplitTime
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
