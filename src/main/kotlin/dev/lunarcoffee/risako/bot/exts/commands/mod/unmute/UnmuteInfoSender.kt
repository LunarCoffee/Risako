package dev.lunarcoffee.risako.bot.exts.commands.mod.unmute

import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.extensions.*
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.ContentSender
import net.dv8tion.jda.api.entities.Member

class UnmuteInfoSender(private val offender: Member) : ContentSender {
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
