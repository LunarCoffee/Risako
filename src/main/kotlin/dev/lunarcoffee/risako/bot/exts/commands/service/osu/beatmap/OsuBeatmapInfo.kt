package dev.lunarcoffee.risako.bot.exts.commands.service.osu.beatmap

import dev.lunarcoffee.risako.bot.exts.commands.service.osu.OsuHasMode
import dev.lunarcoffee.risako.framework.core.std.SplitTime

internal class OsuBeatmapInfo(
    val beatmapId: String,
    val name: String,
    val artist: String,
    val bpm: String,
    val creator: String,
    val cs: String,
    val ar: String,
    val hp: String,
    val od: String,
    val maxCombo: String?,
    private val starRatingRaw: String,
    private val lengthSeconds: String,
    private val statusRaw: String
) : OsuHasMode() {

    override var mode = -1

    val status
        get() = when (statusRaw) {
            "-2" -> "graveyard"
            "-1" -> "WIP"
            "0" -> "pending"
            "1" -> "ranked"
            "2" -> "approved"
            "3" -> "qualified"
            "4" -> "loved"
            else -> throw IllegalStateException()
        }

    val starRating get() = "%.2f".format(starRatingRaw.toDouble())
    val length get() = SplitTime(lengthSeconds.toLong() * 1000)
}
