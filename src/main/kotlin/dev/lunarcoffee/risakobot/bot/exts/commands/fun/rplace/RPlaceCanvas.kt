package dev.lunarcoffee.risakobot.bot.exts.commands.`fun`.rplace

import dev.lunarcoffee.risakobot.bot.consts.ColName
import dev.lunarcoffee.risakobot.bot.exts.commands.`fun`.rplace.gallery.RPlaceGallery
import dev.lunarcoffee.risakobot.framework.core.DB
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import dev.lunarcoffee.risakobot.framework.core.dispatchers.DispatchableArgs
import kotlinx.coroutines.runBlocking
import java.awt.Color

internal object RPlaceCanvas {
    const val IMAGE_PATH = "src/main/resources/rplace/rplace_canvas.png"
    const val CANVAS_SIZE = 40

    // In-memory representation of the canvas.
    private val canvas = Array(CANVAS_SIZE) { Array(CANVAS_SIZE) { Color.WHITE } }
    private val canvasCol = DB.getCollection<Color>(ColName.RPLACE_CANVAS)

    init {
        runBlocking {
            // Fill with white if the canvas in the DB is empty.
            if (canvasCol.find().toList().isEmpty()) {
                canvasCol.insertMany(List(CANVAS_SIZE * CANVAS_SIZE) { Color.WHITE })
            }

            // Load the canvas into the array in memory.
            val dbCanvas = canvasCol
                .find()
                .toList()
                .chunked(CANVAS_SIZE)
                .map { it.toTypedArray() }
                .toTypedArray()
            dbCanvas.copyInto(canvas)
        }
    }

    // Draws and saves a single pixel to the canvas.
    suspend fun drawPixel(ctx: CommandContext, args: DispatchableArgs) {
        RPlaceCanvasDrawer(canvas).putAndSavePixel(ctx, args)
    }

    // If [grid] is true, a grid will be drawn over the canvas' contents. If it is false, there
    // will be no grid, but still the info embed. If it is null, there will be only a raw image.
    suspend fun sendCanvas(ctx: CommandContext, grid: Boolean? = true) {
        RPlaceCanvasSender(canvas).sendCanvas(ctx, grid)
    }

    // Sends the colors that can be used to draw on the canvas.
    suspend fun sendColors(ctx: CommandContext) = RPlaceCanvasSender(canvas).sendColors(ctx)

    // Saves a snapshot for later viewing.
    suspend fun takeSnapshot(ctx: CommandContext, args: DispatchableArgs) {
        RPlaceGallery(canvas).takeSnapshot(ctx, args)
    }

    // Only the owner can delete snapshots.
    suspend fun deleteSnapshot(ctx: CommandContext, args: DispatchableArgs) {
        RPlaceGallery(canvas).deleteSnapshot(ctx, args)
    }

    // Sends a list of all snapshots or a specific snapshot depending on whether or not a name is
    // present in [args].
    suspend fun sendGallery(ctx: CommandContext, args: DispatchableArgs) {
        RPlaceGallery(canvas).sendGallery(ctx, args)
    }

    suspend fun saveCanvas(new: Array<Array<Color>>) {
        // Delete everything and replace with new canvas.
        canvasCol.drop()
        canvasCol.insertMany(new.flatten())
    }
}
