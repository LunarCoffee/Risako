@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.commands.config

import dev.lunarcoffee.risako.bot.consts.GUILD_OVERRIDES
import dev.lunarcoffee.risako.bot.exts.commands.config.sb.StarboardConfigurer
import dev.lunarcoffee.risako.bot.exts.commands.config.sb.StarboardToggler
import dev.lunarcoffee.risako.bot.std.GuildOverrides
import dev.lunarcoffee.risako.framework.api.dsl.command
import dev.lunarcoffee.risako.framework.api.extensions.sendError
import dev.lunarcoffee.risako.framework.api.extensions.sendSuccess
import dev.lunarcoffee.risako.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.commands.transformers.TrWord
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

    fun sb() = command("sb") {
        description = "Configures options for the starboard feature."
        aliases = arrayOf("starboard")

        extDescription = """
            |`$name channel|amount|toggle [value]`\n
            |This command configures options for the starboard. The option is specified as the
            |first argument.
            |&{Changing the starboard channel:}
            |By default, I will look for a channel with `starboard` in its name and use that one.
            |To change the starboard channel, the first argument should be `channel`, and the
            |second should be the channel's name or tag (mention). For example, changing to the
            |channel `#star-zone` could look like `..sb channel #star-zone`.
            |&{Modifying the star requirement:}
            |Normally, once any message gets just one star, it will be sent to the starboard. If
            |you want to change this, you can pass the `amount` option with the minimum star count
            |like so: `..sb amount 2`. This value can be anywhere between 1 and 1000.
            |&{Enabling or disabling:}
            |If you have another starboard bot or just don't want to use the feature, you can
            |toggle it with the `toggle` option: `..sb toggle`. When off, old starboard posts will
            |be retained but won't update with reactions. If you turn starboard back on, those will
            |update with reactions again.
        """

        expectedArgs = arrayOf(TrWord(), TrWord(true))
        execute { args ->
            val option = args.get<String>(0).toLowerCase()
            val value = args.get<String>(1)

            when (option) {
                "channel" -> StarboardConfigurer(this).setChannel(value)
                "amount" -> if (value.toIntOrNull() != null)
                    StarboardConfigurer(this).setRequiredStars(value.toInt())
                else
                    sendError("`${value}` is not a number (or is not between `1` and `1000`)!")
                "toggle" -> StarboardToggler(this).toggle()
                else -> sendError("That is not a valid option!")
            }
        }
    }
}
