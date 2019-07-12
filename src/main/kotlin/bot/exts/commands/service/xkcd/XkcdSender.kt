package bot.exts.commands.service.xkcd

import bot.consts.Emoji
import framework.api.dsl.embed
import framework.api.extensions.send
import framework.core.commands.CommandContext
import framework.core.std.ContentSender

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
