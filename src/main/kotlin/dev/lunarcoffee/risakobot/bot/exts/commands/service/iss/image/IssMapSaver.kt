package dev.lunarcoffee.risakobot.bot.exts.commands.service.iss.image

import dev.lunarcoffee.risakobot.bot.exts.commands.service.iss.stats.IssStats
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import java.awt.RenderingHints as RH

internal class IssMapSaver(private val stats: IssStats) {
    suspend fun saveImage(): File {
        val rawImage = IssMapImageRequester(stats).get()

        // If the command is used too fast, the image might be corrupted. Not sure, should probably
        // investigate further.
        return File(IMAGE_PATH).apply {
            writeBytes(rawImage)
            drawMarkerAndLabel(this)
        }
    }

    private fun drawMarkerAndLabel(file: File) {
        val image = ImageIO.read(file)
        val dotLayer = BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB).apply {
            createGraphics().apply {
                // Make the text look nice.
                setRenderingHint(RH.KEY_ANTIALIASING, RH.VALUE_ANTIALIAS_ON)
                setRenderingHint(RH.KEY_STROKE_CONTROL, RH.VALUE_STROKE_PURE)

                drawImage(image, -20, -20, null)
                paint = Color.RED

                // Draw ISS location marker.
                font = Font(Font.SANS_SERIF, Font.BOLD, 60)
                drawString(Typography.times.toString(), width / 2 - 20, height / 2 + 20)

                // Draw ISS label.
                font = Font(Font.SANS_SERIF, Font.PLAIN, 42)
                drawString("ISS", width / 2 - 28, height / 2 + 68)

                dispose()
            }
        }
        ImageIO.write(dotLayer, "png", file)
    }

    companion object {
        private const val IMAGE_PATH = "src/main/resources/iss/iss_map.png"
        private const val IMAGE_SIZE = 760
    }
}
