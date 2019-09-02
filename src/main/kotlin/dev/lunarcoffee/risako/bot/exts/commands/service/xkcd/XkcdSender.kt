package dev.lunarcoffee.risako.bot.exts.commands.service.xkcd

import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.ContentSender

class XkcdSender(private val which: Int?) : ContentSender {
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
