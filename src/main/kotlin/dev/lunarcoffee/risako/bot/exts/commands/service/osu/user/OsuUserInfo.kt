package dev.lunarcoffee.risako.bot.exts.commands.service.osu.user

import dev.lunarcoffee.risako.bot.exts.commands.service.osu.OsuHasMode
import dev.lunarcoffee.risako.framework.core.std.SplitTime
import java.time.LocalDateTime
import kotlin.math.roundToInt

internal class OsuUserInfo(
    val userId: String,
    val username: String,
    val globalRank: String,
    val countryRank: String,
    val country: String,
    val ssh: String,
    val ss: String,
    val sh: String,
    val s: String,
    val a: String,
    private val accuracyRaw: String,
    private val ppRaw: String,
    private val joinTimeRaw: String,
    private val playTimeSeconds: String
) : OsuHasMode() {

    override var mode = -1

    val accuracy get() = "%.2f".format(accuracyRaw.toDouble())
    val pp get() = ppRaw.toDouble().roundToInt()

    val joinTime get() = LocalDateTime.parse(joinTimeRaw.replace(" ", "T"))!!
    val playTime get() = SplitTime(playTimeSeconds.toLong() * 1_000)
}
