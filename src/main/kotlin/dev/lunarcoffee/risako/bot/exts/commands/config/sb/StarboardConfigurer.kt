package dev.lunarcoffee.risako.bot.exts.commands.config.sb

import dev.lunarcoffee.risako.bot.consts.ColName
import dev.lunarcoffee.risako.bot.consts.GUILD_OVERRIDES
import dev.lunarcoffee.risako.bot.exts.listeners.starboard.StarboardEntry
import dev.lunarcoffee.risako.bot.std.GuildOverrides
import dev.lunarcoffee.risako.framework.api.extensions.sendError
import dev.lunarcoffee.risako.framework.api.extensions.sendSuccess
import dev.lunarcoffee.risako.framework.core.DB
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import org.litote.kmongo.eq
import org.litote.kmongo.set

class StarboardConfigurer(private val ctx: CommandContext) {
    suspend fun setRequiredStars(amount: Int) {
        if (amount !in 1..1_000) {
            ctx.sendError("The required star amount has to be between `1` and `1000`!")
            return
        }

        GUILD_OVERRIDES.updateOne(
            getOrCreateOverrides().isSame(),
            set(GuildOverrides::starboardRequirement, amount)
        )
        ctx.sendSuccess("The minimum amount of stars is now `${amount}`!")
    }

    suspend fun setChannel(id: String) {
        val channelByName = ctx.event.guild.getTextChannelsByName(id, true).firstOrNull()
        if (!(id matches CHANNEL_REGEX) && channelByName == null) {
            ctx.sendError("I can't find that channel!")
            return
        }

        // Try using the channel by name, then resort to trying the ID.
        val channel = channelByName
            ?: ctx.jda.getTextChannelById(CHANNEL_REGEX.matchEntire(id)!!.groupValues[1])

        if (channel == null) {
            ctx.sendError("I can't find that channel!")
            return
        }

        val overrides = getOrCreateOverrides()

        // Get the original channel to remove all their entries from the database to avoid errors.
        val originalChannel = if (overrides.starboardChannel != null)
            ctx.jda.getTextChannelById(overrides.starboardChannel)
        else
            ctx.event.guild.textChannels.find { "starboard" in it.name }

        if (originalChannel != null) {
            for (entryId in originalChannel.iterableHistory.map { it.id })
                col.deleteOne(StarboardEntry::entryMessageId eq entryId)
        }

        GUILD_OVERRIDES.updateOne(
            overrides.isSame(),
            set(GuildOverrides::starboardChannel, channel.id)
        )
        ctx.sendSuccess("The starboard channel has been set!")
    }

    private suspend fun getOrCreateOverrides(): GuildOverrides {
        var override = GUILD_OVERRIDES.findOne(GuildOverrides::guildId eq ctx.event.guild.id)
        if (override == null) {
            GUILD_OVERRIDES.insertOne(
                GuildOverrides(ctx.event.guild.id, false, false, true).apply { override = this }
            )
        }
        return override!!
    }

    companion object {
        private val CHANNEL_REGEX = """<#(\d{18})>""".toRegex()
        private val col = DB.getCollection<StarboardEntry>(ColName.STARBOARD)
    }
}
