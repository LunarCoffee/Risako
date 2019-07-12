package bot.exts.commands.`fun`.rplace

import java.awt.*
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import java.awt.RenderingHints as RH

// Saves the contents of the passed [canvas] to the disk. This does not update the DB.
internal class RPlaceCanvasSaver(private val canvas: Array<Array<Color>>) {
    fun createAndSaveImage(grid: Boolean = false) {
        val file = File(RPlaceCanvas.IMAGE_PATH)
        val image = BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB).apply {
            createGraphics().apply {
                // Make the text look nice.
                setRenderingHint(RH.KEY_ANTIALIASING, RH.VALUE_ANTIALIAS_ON)
                setRenderingHint(RH.KEY_STROKE_CONTROL, RH.VALUE_STROKE_PURE)

                // Fill background with white.
                fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE)

                if (grid) {
                    drawWithGrid()
                } else {
                    drawWithoutGrid()
                }
            }
        }
        ImageIO.write(image, "png", file)
    }

    // Draws the contents of the canvas with a grid and a pair of axes to help users find the point
    // where they want to put their next pixel.
    private fun Graphics2D.drawWithGrid() {
        paint = Color.BLACK
        font = Font(Font.SANS_SERIF, Font.PLAIN, 16)

        for (coord in 1..RPlaceCanvas.CANVAS_SIZE) {
            // Draw x and y axes coordinate labels, respectively.
            drawString(coord.toString(), (if (coord < 10) 40 else 35) + coord * 30, 40)
            drawString(coord.toString(), 20, 52 + coord * 30)
        }

        // Draw x and y axes, respectively.
        drawLine(55, 55, IMAGE_SIZE - 25, 55)
        drawLine(55, 55, 55, IMAGE_SIZE - 25)

        for (xC in 0 until RPlaceCanvas.CANVAS_SIZE) {
            for (yC in 0 until RPlaceCanvas.CANVAS_SIZE) {
                paint = canvas[yC][xC]

                // Draw the individual pixel (30x30).
                fillRect(60 + xC * 30, 60 + yC * 30, 30, 30)

                // Draw the grid. This method is quite inefficient compared to drawing lines. Maybe
                // do that instead?
                paint = Color.decode("#CCCCCC")
                drawRect(60 + xC * 30, 60 + yC * 30, 30, 30)
            }
        }
        dispose()
    }

    // Draws the contents of the canvas without a grid or any axes. Preferable for viewing.
    private fun Graphics2D.drawWithoutGrid() {
        val pixelSize = IMAGE_SIZE.toDouble() / RPlaceCanvas.CANVAS_SIZE

        for (xC in 0 until RPlaceCanvas.CANVAS_SIZE) {
            for (yC in 0 until RPlaceCanvas.CANVAS_SIZE) {
                paint = canvas[yC][xC]
                fill(
                    Rectangle2D.Double(
                        xC * pixelSize,
                        yC * pixelSize,
                        pixelSize + 0.5,
                        pixelSize + 0.5
                    )
                )
            }
        }
    }

    companion object {
        private const val IMAGE_SIZE = 30 * RPlaceCanvas.CANVAS_SIZE + 90
    }
}
