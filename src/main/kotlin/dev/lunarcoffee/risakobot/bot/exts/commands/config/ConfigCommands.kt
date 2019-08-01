@file:Suppress("unused")

package dev.lunarcoffee.risakobot.bot.exts.commands.config

import dev.lunarcoffee.risakobot.bot.consts.GUILD_OVERRIDES
import dev.lunarcoffee.risakobot.bot.std.GuildOverrides
import dev.lunarcoffee.risakobot.framework.api.dsl.command
import dev.lunarcoffee.risakobot.framework.api.extensions.sendSuccess
import dev.lunarcoffee.risakobot.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risakobot.framework.core.bot.Bot
import org.litote.kmongo.eq
import org.litote.kmongo.set

@CommandGroup("Config")
internal class ConfigCommands(private val bot: Bot) {
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
                            insertOne(GuildOverrides(event.guild.id, false, true))
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
                            insertOne(GuildOverrides(event.guild.id, true, true))
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
}
