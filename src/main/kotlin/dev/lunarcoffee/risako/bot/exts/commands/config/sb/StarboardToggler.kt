package dev.lunarcoffee.risako.bot.exts.commands.config.sb

import dev.lunarcoffee.risako.bot.consts.GUILD_OVERRIDES
import dev.lunarcoffee.risako.bot.std.GuildOverrides
import dev.lunarcoffee.risako.framework.api.extensions.sendSuccess
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import org.litote.kmongo.eq
import org.litote.kmongo.set

class StarboardToggler(private val ctx: CommandContext) {
    suspend fun toggle() {
        GUILD_OVERRIDES.run {
            val override = findOne(GuildOverrides::guildId eq ctx.event.guild.id)
            ctx.sendSuccess(
                when {
                    override == null -> {
                        insertOne(GuildOverrides(ctx.event.guild.id, false, false, true))
                        "Disabled the starboard feature!"
                    }
                    override.noStarboard -> {
                        updateOne(override.isSame(), set(GuildOverrides::noStarboard, false))
                        "Enabled the starboard feature!"
                    }
                    else -> {
                        updateOne(override.isSame(), set(GuildOverrides::noStarboard, true))
                        "Disabled the starboard feature!"
                    }
                }
            )
        }
    }
}
