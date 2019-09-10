package dev.lunarcoffee.risako.bot.std

import org.litote.kmongo.eq

class GuildOverrides(
    val guildId: String,
    val noPayRespects: Boolean,
    val noSuggestCommands: Boolean,
    val noStarboard: Boolean
) {
    val starboardRequirement = 1
    val starboardChannel: String? = null

    fun isSame() = ::guildId eq guildId
}
