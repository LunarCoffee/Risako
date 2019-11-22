package dev.lunarcoffee.risako.bot.exts.commands.`fun`.rplace.gallery

import dev.lunarcoffee.risako.bot.exts.commands.`fun`.rplace.RPlaceCanvas
import dev.lunarcoffee.risako.bot.exts.commands.`fun`.rplace.RPlaceCanvasSaver
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.api.extensions.sendError
import dev.lunarcoffee.risako.framework.api.extensions.sendSuccess
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.dispatchers.DispatchableArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

// Takes and deletes snapshots of the passed [canvas]. A new instance should be constructed for
// each time the canvas is updated.
class RPlaceGallery(private val canvas: Array<Array<Color>>) {
    suspend fun takeSnapshot(ctx: CommandContext, args: DispatchableArgs) {
        val cleanName = args.get<String>(3).replace("=", "_")
        if (cleanName.length !in 1..20) {
            ctx.sendError(
                if (cleanName.isEmpty())
                    "I need a name to give the snapshot!"
                else
                    "I can't name a snapshot that name!"
            )
            return
        }

        // The name of each snapshot file is formatted like "nameOfSnapshot=timeCreatedMs.png."
        val filename = "$cleanName=${System.currentTimeMillis()}.png"
        val file = File("$SNAPSHOT_PATH/$filename")

        // Don't allow duplicates.
        if (cleanName in File(SNAPSHOT_PATH).walk().map { it.name.substringBefore("=") }) {
            ctx.sendError("A snapshot with that name already exists!")
            return
        }

        // First save the image without a grid.
        RPlaceCanvasSaver(canvas).createAndSaveImage()

        // Scale the image down to save memory.
        val scaledImage = toBufferedImage(
            withContext(Dispatchers.IO) { ImageIO.read(File(RPlaceCanvas.IMAGE_PATH)) }
                .getScaledInstance(300, 300, Image.SCALE_DEFAULT)
        )

        withContext(Dispatchers.IO) {
            ImageIO.write(scaledImage, "png", file)
            ctx.sendSuccess("The snapshot was saved!")
        }
    }

    suspend fun deleteSnapshot(ctx: CommandContext, args: DispatchableArgs) {
        // Make sure the one deleting the snapshot is the owner.
        if (ctx.event.author.id != ctx.bot.config.ownerId) {
            val ownerTag = ctx.jda.getUserById(ctx.bot.config.ownerId)!!.asTag
            ctx.sendError("Contact `$ownerTag` to request a snapshot deletion.")
            return
        }

        val name = args.get<String>(3)
        try {
            withContext(Dispatchers.IO) {
                // Simply deleting the image will do. We also don't need to check for the return of
                // [delete] since it only returns [false] when the file does not exist, which is
                // handled by the try/catch.
                Files
                    .newDirectoryStream(Paths.get(SNAPSHOT_PATH), "$name=*.png")
                    .first()
                    .toFile()
                    .delete()
                ctx.sendSuccess("The snapshot has been deleted!")
            }
        } catch (e: NoSuchElementException) {
            ctx.sendError("There is no snapshot with that name!")
            return
        }
    }

    suspend fun sendGallery(ctx: CommandContext, args: DispatchableArgs) {
        ctx.send(RPlaceGallerySender(args))
    }

    private fun toBufferedImage(image: Image): BufferedImage {
        return BufferedImage(
            image.getWidth(null),
            image.getHeight(null),
            BufferedImage.TYPE_INT_ARGB
        ).apply {
            createGraphics().apply {
                drawImage(image, 0, 0, null)
                dispose()
            }
        }
    }

    companion object {
        const val SNAPSHOT_PATH = "src/main/resources/rplace/snapshots"
    }
}
