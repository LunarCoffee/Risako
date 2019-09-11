package dev.lunarcoffee.risako.bot.std

import dev.lunarcoffee.risako.bot.consts.GUILD_OVERRIDES
import net.dv8tion.jda.api.entities.Member
import org.litote.kmongo.eq

class GuildOverrides(
    val guildId: String,
    val noPayRespects: Boolean,
    val noSuggestCommands: Boolean,
    val noStarboard: Boolean
) {
    val starboardRequirement = 1
    val starboardChannel: String? = null

    val risakoConfigurerRole: String? = null

    fun isSame() = ::guildId eq guildId
    fun canConfigureBot(member: Member): Boolean? {
        return if (risakoConfigurerRole != null)
            risakoConfigurerRole in member.roles.map { it.id }
        else
            null
    }

    companion object {
        suspend fun getOrCreateOverrides(guildId: String): GuildOverrides {
            var override = GUILD_OVERRIDES.findOne(GuildOverrides::guildId eq guildId)
            if (override == null) {
                GUILD_OVERRIDES.insertOne(
                    GuildOverrides(guildId, false, false, true).apply { override = this }
                )
            }
            return override!!
        }
    }
}
