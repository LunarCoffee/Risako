package dev.lunarcoffee.risako.bot.exts.commands.`fun`.eightball

import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.ContentSender

internal class EightBallSender : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.send(
            embed {
                title = "${Emoji.BILLIARD_BALL}  The 8-ball says:"
                description = responses.random()
            }
        )
    }

    companion object {
        private val responses = arrayOf(
            "It is certain.",
            "It is decidedly so.",
            "Without a doubt.",
            "Yes - definitely.",
            "You may rely on it.",
            "As I see it, yes.",
            "Most likely.",
            "Outlook good.",
            "Yes.",
            "Signs point to yes.",
            "Reply hazy, try again.",
            "Ask again later.",
            "Better not tell you now.",
            "Cannot predict now.",
            "Concentrate and ask again.",
            "Don't count on it.",
            "My reply is no.",
            "My sources say no.",
            "Outlook not so good.",
            "Very doubtful."
        )
    }
}
