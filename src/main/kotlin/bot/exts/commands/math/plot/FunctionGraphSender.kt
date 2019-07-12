package bot.exts.commands.math.plot

import bot.consts.Emoji
import framework.api.dsl.embed
import framework.api.extensions.await
import framework.api.extensions.sendError
import framework.core.commands.CommandContext
import framework.core.std.ContentSender
import java.io.File

internal class FunctionGraphSender(private val plotter: FunctionPlotter) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        if (plotter.functionStrings.any { it.length >= 70 }) {
            ctx.sendError("Your function must be represented in less than 70 characters!")
            return
        }

        val image = File(
            try {
                plotter.plot()
            } catch (e: NullPointerException) {
                // This is caught when a function is invalid.
                ctx.sendError("One or more of your functions was invalid!")
                return
            }
        )

        ctx.sendMessage(
            embed {
                val numFunctions = plotter.functionStrings.size
                val functionOrNumber = if (numFunctions == 1) {
                    "**${plotter.functionStrings[0].replace("*", "\\*")}**"
                } else {
                    "**$numFunctions** functions"
                }

                title = "${Emoji.CHART_UPWARDS_TREND}  Graph of $functionOrNumber:"
                description = plotter.functionStrings.mapIndexed { index, string ->
                    "**#${index + 1}**: y=${string.replace("*", "\\*")}"
                }.joinToString("\n")

                image { url = "attachment://${image.name}" }
            }
        ).addFile(image).await()
        image.delete()
    }
}
