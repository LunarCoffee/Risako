package dev.lunarcoffee.risako.bot.exts.commands.`fun`.rplace

import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.bot.exts.commands.`fun`.rplace.info.RPlaceInfoManager
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.extensions.await
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import java.awt.Color
import java.io.File

// Sends either the contents of a canvas, or the colors that can be used for drawing. The canvas to
// be sent should be passed as [canvas], and a new instance should be created every time the canvas
// is changed.
class RPlaceCanvasSender(private val canvas: Array<Array<Color>>) {
    suspend fun sendCanvas(ctx: CommandContext, grid: Boolean? = true) {
        val saver = RPlaceCanvasSaver(canvas)
        saver.createAndSaveImage(grid ?: false)

        // When [grid] is null, we send only the image without a grid, no embed.
        if (grid == null) {
            saver.createAndSaveImage(false)
            ctx.sendFile(File(RPlaceCanvas.IMAGE_PATH)).queue()
            return
        }

        ctx.sendMessage(
            embed {
                title = "${Emoji.WHITE_SQUARE_BUTTON}  Current canvas stats:"
                description = """
                    |**Total pixels put**: ${RPlaceInfoManager.totalPixelsPut}
                    |**Total contributors**: ${RPlaceInfoManager.totalContributors}
                """.trimMargin()

                footer { text = "Type ..rplace colors to see all available colors." }
            }
        ).addFile(File(RPlaceCanvas.IMAGE_PATH)).await()
    }

    suspend fun sendColors(ctx: CommandContext) {
        ctx.send(
            embed {
                title = "${Emoji.WHITE_SQUARE_BUTTON}  Available colors:"
                description += "[red, orange, yellow, green, blue, purple/violet, white, "
                description += "grey/gray, dgrey/dgray, black, brown, pink, magenta]"
            }
        )
    }
}
