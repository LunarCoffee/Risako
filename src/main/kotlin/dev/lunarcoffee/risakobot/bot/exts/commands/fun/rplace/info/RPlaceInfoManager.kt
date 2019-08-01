package dev.lunarcoffee.risakobot.bot.exts.commands.`fun`.rplace.info

import dev.lunarcoffee.risakobot.framework.core.DB
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.eq
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

internal object RPlaceInfoManager {
    private val canvasInfoCol = DB.getCollection<RPlaceCanvasInfo>("RPlaceCanvasInfo0")
    private val canvasInfo = runBlocking {
        canvasInfoCol.run {
            // Get or insert the existing [RPlaceCanvasInfo].
            if (find().toList().isEmpty()) {
                RPlaceCanvasInfo(0, mutableSetOf()).apply { org.litote.kmongo.insertOne(this) }
            } else {
                find().first()!!
            }
        }
    }

    var totalPixelsPut = AtomicLong(canvasInfo.totalPixelsPut)
    var totalContributors = AtomicInteger(canvasInfo.contributors.size)

    // Saves pixel count and contributor info the the DB.
    suspend fun storeInfoUpdates(contributorId: Long) {
        canvasInfo.totalPixelsPut = totalPixelsPut.incrementAndGet()

        canvasInfo.contributors += contributorId
        totalContributors.set(canvasInfo.contributors.size)

        canvasInfoCol.updateOne(
            RPlaceCanvasInfo::totalPixelsPut eq totalPixelsPut.get() - 1,
            canvasInfo
        )
    }
}
