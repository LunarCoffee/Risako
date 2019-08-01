package dev.lunarcoffee.risakobot.bot.exts.commands.`fun`.rplace.gallery

import dev.lunarcoffee.risakobot.bot.consts.Emoji
import dev.lunarcoffee.risakobot.framework.api.dsl.embed
import dev.lunarcoffee.risakobot.framework.api.dsl.embedPaginator
import dev.lunarcoffee.risakobot.framework.api.extensions.*
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import dev.lunarcoffee.risakobot.framework.core.dispatchers.DispatchableArgs
import dev.lunarcoffee.risakobot.framework.core.std.ContentSender
import dev.lunarcoffee.risakobot.framework.core.std.SplitTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

// Sends saved snapshots of the canvas. This part is relatively detached from the rest, interacting
// only with the filesystem and no other [RPlace*] classes. A new instance should be constructed
// for every command invocation.
internal class RPlaceGallerySender(private val args: DispatchableArgs) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        val name = args.get<String>(3)
        if (name.isEmpty()) {
            sendAllSnapshots(ctx)
        } else {
            sendSingleSnapshot(ctx, name)
        }
    }

    // Sends all taken snapshots, not including their images (that would stress the network too
    // much, and possibly the disk).
    private suspend fun sendAllSnapshots(ctx: CommandContext) {
        // Make a string of all of the snapshots' names and creation times.
        val snapshots = File(RPlaceGallery.SNAPSHOT_PATH)
            .walk()
            .drop(1)
            .map {
                val snapshotName = it.name.substringBefore("=")
                val time = getSnapshotTime(it.name)

                // Time to sort by (the above [time] is a formatted string).
                val timeMs = it.name.substringAfterLast("=").substringBefore(".").toLong()

                Triple("**$snapshotName**: $time", time, timeMs)
            }
            .sortedByDescending { it.third }  // Sort by time created.
            .chunked(16)
            .map { pairs -> pairs.joinToString("\n") { it.first } }

        if (snapshots.count() == 0) {
            ctx.sendSuccess("There are no canvas snapshots!")
            return
        }

        ctx.send(
            ctx.embedPaginator {
                for (group in snapshots) {
                    page(
                        embed {
                            title = "${Emoji.SNOW_CAPPED_MOUNTAIN}  Canvas snapshots:"
                            description = group
                        }
                    )
                }
            }
        )
    }

    // Sends the raw image of a single snapshot along with its name and creation time.
    private suspend fun sendSingleSnapshot(ctx: CommandContext, name: String) {
        val path = try {
            withContext(Dispatchers.IO) {
                Files
                    .newDirectoryStream(Paths.get(RPlaceGallery.SNAPSHOT_PATH), "$name=*.png")
                    .first()
                    .toFile()
                    .path
            }
        } catch (e: NoSuchElementException) {
            ctx.sendError("There is no snapshot with that name!")
            return
        }

        ctx.sendMessage(
            embed {
                title = "${Emoji.SNOW_CAPPED_MOUNTAIN}  Canvas snapshot info:"
                description = """
                    |**Name**: $name
                    |**Time created**: ${getSnapshotTime(path)}
                """.trimMargin()
            }
        ).addFile(File(path)).queue()
    }

    // Extracts snapshot creation time from the image's filename.
    private fun getSnapshotTime(filename: String): String {
        val timeMs = filename
            .substringAfterLast("=")
            .substringBefore(".")
            .toLong() - System.currentTimeMillis()
        return SplitTime(timeMs).localWithoutWeekday()
    }
}