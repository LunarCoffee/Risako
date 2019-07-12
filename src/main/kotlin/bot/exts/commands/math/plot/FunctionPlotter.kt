package bot.exts.commands.math.plot

import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.roundToInt
import java.awt.RenderingHints as RH

internal class FunctionPlotter(val functionStrings: List<String>) {
    private val imageName = "src/main/resources/plot/${functionStrings.hashCode()}.png"

    fun plot(): String {
        saveImage()
        return imageName
    }

    private fun saveImage() {
        val file = File(imageName)
        val image = BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB).apply {
            createGraphics().apply {
                // Make the text look nice.
                setRenderingHint(RH.KEY_ANTIALIASING, RH.VALUE_ANTIALIAS_ON)
                setRenderingHint(RH.KEY_STROKE_CONTROL, RH.VALUE_STROKE_PURE)

                // Fill background with white.
                fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE)

                drawFunctions()
                drawAxes()
            }
        }
        ImageIO.write(image, "png", file)
    }

    private fun Graphics2D.drawAxes() {
        paint = Color.BLACK

        // Draw the x and y axes.
        drawLine(IMAGE_SIZE - PRECISION * INTERVAL * 2, ORIGIN, PRECISION * INTERVAL * 2, ORIGIN)
        drawLine(ORIGIN, IMAGE_SIZE - PRECISION * INTERVAL * 2, ORIGIN, PRECISION * INTERVAL * 2)

        // Draw ticks and labels to mark intervals.
        for (n in 1 until 2 * PRECISION / MULTIPLIER) {
            // Avoid creating an extra cross or making labels that cover the crosses at the origin.
            if (n == PRECISION / MULTIPLIER) {
                continue
            }

            // Draw ticks.
            drawLine(n * MULTIPLIER, ORIGIN - 8, n * MULTIPLIER, ORIGIN + 8)
            drawLine(ORIGIN - 8, n * MULTIPLIER, ORIGIN + 8, n * MULTIPLIER)

            // Get the correct offset for the different widths of the numbers on the interval
            // labels to correctly center them.
            val xLabel = n * MULTIPLIER - when (n - PRECISION / MULTIPLIER) {
                in 1..9 -> 4
                in 10..99, in -9..-1 -> 8
                else -> 12
            }

            // Draw interval labels.
            drawString((n - PRECISION / MULTIPLIER).toString(), xLabel, ORIGIN - 16)
            drawString((PRECISION / MULTIPLIER - n).toString(), ORIGIN + 16, n * MULTIPLIER + 4)
        }
    }

    private fun Graphics2D.drawFunctions() {
        for ((index, string) in functionStrings.withIndex()) {
            val evaluator = FunctionEvaluator(string)
            for (x in -PRECISION until PRECISION) {
                val y = evaluator.evaluate(x.toDouble() / MULTIPLIER)!! * INTERVAL * MULTIPLIER
                val nextY = evaluator.evaluate((x + 1.0) / MULTIPLIER)!! * INTERVAL * MULTIPLIER

                // Try to ignore holes/asymptotes/gaps.
                if (y.isNaN() || nextY.isNaN() || abs(nextY - y) > 2_500) {
                    continue
                }

                paint = COLORS[index]
                drawLine(
                    ORIGIN + x * INTERVAL,
                    (IMAGE_SIZE - ORIGIN - y).roundToInt(),
                    ORIGIN + (x + 1) * INTERVAL,
                    (IMAGE_SIZE - ORIGIN - nextY).roundToInt()
                )
            }
        }
    }

    companion object {
        private const val IMAGE_SIZE = 800
        private const val ORIGIN = IMAGE_SIZE / 2
        private const val PRECISION = 400
        private const val INTERVAL = 1
        private const val MULTIPLIER = 40

        // Colors to use when plotting functions.
        private val COLORS = arrayOf(
            Color.RED,
            Color.BLUE,
            Color.decode("#19AA06"),
            Color.decode("#EF9F00"),
            Color.MAGENTA
        )
    }
}
