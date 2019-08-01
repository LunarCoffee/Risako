package dev.lunarcoffee.risakobot.bot.exts.commands.`fun`.rplace

import dev.lunarcoffee.risakobot.bot.exts.commands.`fun`.rplace.cooldown.RPlaceCooldownManager
import dev.lunarcoffee.risakobot.bot.exts.commands.`fun`.rplace.info.RPlaceInfoManager
import dev.lunarcoffee.risakobot.framework.api.extensions.sendError
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import dev.lunarcoffee.risakobot.framework.core.dispatchers.DispatchableArgs
import java.awt.Color

// Performs drawing operations on the passed [canvas], then loads them into the main [RPlaceCanvas]
// object for later use. This is the only class that modifies the actual canvas.
internal class RPlaceCanvasDrawer(private val canvas: Array<Array<Color>>) {
    suspend fun putAndSavePixel(ctx: CommandContext, args: DispatchableArgs) {
        val x = args.get<Int>(1) - 1
        val y = args.get<Int>(2) - 1

        val color = when (args.get<String>(3)) {
            "red" -> Color.RED
            "orange" -> Color.decode("#FFB200")
            "yellow" -> Color.YELLOW
            "green" -> Color.GREEN
            "blue" -> Color.decode("#478DFF")
            "purple", "violet" -> Color.decode("#9E42F4")
            "white" -> Color.WHITE
            "grey", "gray" -> Color.GRAY
            "dgrey", "dgray" -> Color.DARK_GRAY
            "black" -> Color.BLACK
            "brown" -> Color.decode("#704107")
            "pink" -> Color.PINK
            "magenta" -> Color.MAGENTA
            "" -> null
            else -> {
                ctx.sendError("That isn't a valid color! Type `..rplace colors` for more info.")
                return
            }
        }

        // Ensure all arguments were given (they're all optional due to the <view> operation).
        if (x < 0 || y < 0 || color == null) {
            ctx.sendError("You didn't provide all the arguments for the operation `put`!")
            return
        }

        if (drawPixel(ctx, x, y, color)) {
            RPlaceCanvasSender(canvas).sendCanvas(ctx)
        }
    }

    private suspend fun drawPixel(ctx: CommandContext, x: Int, y: Int, color: Color): Boolean {
        if (x !in 0 until RPlaceCanvas.CANVAS_SIZE || y !in 0 until RPlaceCanvas.CANVAS_SIZE) {
            ctx.sendError("Those coordinates are off the canvas!")
            return false
        }

        // Check if the user is currently on cooldown.
        val user = ctx.event.author
        val remainingTime = RPlaceCooldownManager.getRemainingCooldown(user.id)
        if (remainingTime != null) {
            ctx.sendError("You can't place another pixel for `$remainingTime`!")
            return false
        }

        canvas[y][x] = color

        // Update the total pixel count and the contributors, and save the new canvas.
        RPlaceInfoManager.storeInfoUpdates(ctx.event.author.idLong)
        RPlaceCanvas.saveCanvas(canvas)

        // Draw the new point to the image and save it so we can send it.
        RPlaceCanvasSaver(canvas).createAndSaveImage()

        // Schedule a new cooldown.
        RPlaceCooldownManager.schedule(ctx, user.id)
        return true
    }
}
