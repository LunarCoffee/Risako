package dev.lunarcoffee.risakobot.bot.exts.commands.mod.unmute

import dev.lunarcoffee.risakobot.bot.consts.Emoji
import dev.lunarcoffee.risakobot.framework.api.dsl.embed
import dev.lunarcoffee.risakobot.framework.api.extensions.*
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import dev.lunarcoffee.risakobot.framework.core.std.ContentSender
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
