package dev.lunarcoffee.risakobot.bot.exts.commands.service.iss.stats

import kotlin.math.abs

internal class IssStats(
    val longitude: Double,
    val latitude: Double,
    val altitude: Double,
    val velocity: Double
) {
    val longitudeStr get() = if (longitude < 0) "${abs(longitude)}°W" else "$longitude°E"
    val latitudeStr get() = if (latitude < 0) "${abs(latitude)}°S" else "$latitude°N"
}
