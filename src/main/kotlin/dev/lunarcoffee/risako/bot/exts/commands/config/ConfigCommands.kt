@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.commands.config

import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.bot.consts.GUILD_OVERRIDES
import dev.lunarcoffee.risako.bot.std.GuildOverrides
import dev.lunarcoffee.risako.framework.api.dsl.command
import dev.lunarcoffee.risako.framework.api.extensions.sendSuccess
import dev.lunarcoffee.risako.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import org.litote.kmongo.eq
import org.litote.kmongo.set

@CommandGroup("Config")
class ConfigCommands(private val bot: Bot) {
    fun togglecs() = command("togglecs") {
        description = "Toggles suggestions for when you type something wrong."
        aliases = arrayOf("togglesuggestions", "togglecommandsuggestions")

        extDescription = """
            |`$name`\n
            |When you type a command and spell the name wrong, I might try and guess what you were
            |trying to do. This comes in the form of command suggestions, which suggest a potential
            |command that has a name that is close to what you typed. They delete themselves after
            |five seconds as to not clog up the channel. This command toggles the sending of these
            |suggestions.
        """

        execute {
            GUILD_OVERRIDES.run {
                val override = findOne(GuildOverrides::guildId eq event.guild.id)
                sendSuccess(
                    when {
                        override == null -> {
                            insertOne(GuildOverrides(event.guild.id, false, true, false))
                            "Disabled command suggestions!"
                        }
                        override.noSuggestCommands -> {
                            updateOne(
                                override.isSame(),
                                set(GuildOverrides::noSuggestCommands, false)
                            )
                            "Enabled command suggestions!"
                        }
                        else -> {
                            updateOne(
                                override.isSame(),
                                set(GuildOverrides::noSuggestCommands, true)
                            )
                            "Disabled command suggestions!"
                        }
                    }
                )
            }
        }
    }

    fun togglef() = command("togglef") {
        description = "Toggles the fancy F to pay respects embed."
        aliases = arrayOf("togglepayrespects")

        extDescription = """
            |`$name`\n
            |When you type `f` or `F`, by default, I will replace your message with a fancy embed
            |that allows other people to react to it with a regional indicator F emoji. When such a
            |reaction is added, it adds their name to the list on the embed. This command toggles
            |that behavior.
        """

        execute {
            GUILD_OVERRIDES.run {
                val override = findOne(GuildOverrides::guildId eq event.guild.id)
                sendSuccess(
                    when {
                        override == null -> {
                            insertOne(GuildOverrides(event.guild.id, true, false, false))
                            "Disabled the pay respects embed!"
                        }
                        override.noPayRespects -> {
                            updateOne(override.isSame(), set(GuildOverrides::noPayRespects, false))
                            "Enabled the pay respects embed!"
                        }
                        else -> {
                            updateOne(override.isSame(), set(GuildOverrides::noPayRespects, true))
                            "Disabled the pay respects embed!"
                        }
                    }
                )
            }
        }
    }

    fun togglesb() = command("togglesb") {
        description = "Toggles the starboard feature."
        aliases = arrayOf("togglestarboard")

        extDescription = """
            |`$name`\n
            |When you react to a message with a star (this one -> ${Emoji.STAR}), I will normally
            |try to find a channel with `starboard` in its name. I will send a message there with
            |the content of the message you reacted to. This is basically a global pin system, but
            |if you wish to disable/enable it, use this command.
        """

        execute {
            GUILD_OVERRIDES.run {
                val override = findOne(GuildOverrides::guildId eq event.guild.id)
                sendSuccess(
                    when {
                        override == null -> {
                            insertOne(GuildOverrides(event.guild.id, false, false, true))
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
}
