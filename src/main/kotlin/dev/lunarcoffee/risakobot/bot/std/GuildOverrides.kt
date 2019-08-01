package dev.lunarcoffee.risakobot.bot.std

import org.litote.kmongo.eq

internal class GuildOverrides(
    val guildId: String,
    val noPayRespects: Boolean,
    val noSuggestCommands: Boolean
) {
    fun isSame() = ::guildId eq guildId
}
