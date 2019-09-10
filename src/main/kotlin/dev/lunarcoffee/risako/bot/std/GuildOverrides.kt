package dev.lunarcoffee.risako.bot.std

import org.litote.kmongo.eq

class GuildOverrides(
    val guildId: String,
    val noPayRespects: Boolean,
    val noSuggestCommands: Boolean,
    val noStarboard: Boolean
) {
    fun isSame() = ::guildId eq guildId
}
