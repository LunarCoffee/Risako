package dev.lunarcoffee.risakobot.bot.exts.commands.service.xkcd

import dev.lunarcoffee.risakobot.bot.consts.Emoji
import dev.lunarcoffee.risakobot.framework.api.dsl.embed
import dev.lunarcoffee.risakobot.framework.api.extensions.send
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import dev.lunarcoffee.risakobot.framework.core.std.ContentSender

internal class XkcdSender(private val which: Int?) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.send(
            XkcdRequester(which).get().run {
                embed {
                    title = "${Emoji.FRAMED_PICTURE}  Xkcd comic #**$num**:"
                    description = """
                        |**Title**: ${this@run.title}
                        |**Alt text**: $alt
                        |**Release date**: $date
                    """.trimMargin()

                    image { url = img }
                }
            }
        )
    }
}
