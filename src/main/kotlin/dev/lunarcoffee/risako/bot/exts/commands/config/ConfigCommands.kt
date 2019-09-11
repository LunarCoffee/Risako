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
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.commands.transformers.TrRest
import dev.lunarcoffee.risako.framework.core.commands.transformers.TrWord
import net.dv8tion.jda.api.Permission
import org.litote.kmongo.set

@CommandGroup("Config")
class ConfigCommands(private val bot: Bot) {
    fun configrole() = command("configrole") {
        description = "Sets the role required to use commands in this category."
        aliases = arrayOf("setconfigrole")

        extDescription = """
            |`$name rolename|roleid`\n
            |In order to use any command in the config category, you must either have a selected
            |role or have the administrator permission. There is no default role, but you can set 
            |it with this command, like: `configrole Risako Manager` if the role is called `Risako 
            |Manager`. Instead of passing the name, you can also pass the role's ID.
        """

        expectedArgs = arrayOf(TrRest())
        execute { args ->
            // Only an admin can set the configurer role.
            if (!event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
                sendError("You need to be an administrator to use this command!")
                return@execute
            }

            // Try getting the role by name, then by ID.
            val roleNameOrId = args.get<String>(0)
            val role = event.guild.getRolesByName(roleNameOrId, true).firstOrNull()
                ?: if (roleNameOrId.toLongOrNull() != null)
                    event.guild.getRoleById(roleNameOrId)
                else
                    null

            if (role == null) {
                sendError("I can't find a role with that name or ID!")
                return@execute
            }

            val overrides = GuildOverrides.getOrCreateOverrides(event.guild.id)
            GUILD_OVERRIDES.updateOne(
                overrides.isSame(),
                set(GuildOverrides::risakoConfigurerRole, role.id)
            )
            sendSuccess("The configurer role has been updated!")
        }
    }

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
            val overrides = GuildOverrides.getOrCreateOverrides(event.guild.id)
            if (!checkConfigurerRole(overrides))
                return@execute

            GUILD_OVERRIDES.run {
                sendSuccess(
                    when {
                        overrides.noSuggestCommands -> {
                            updateOne(
                                overrides.isSame(),
                                set(GuildOverrides::noSuggestCommands, false)
                            )
                            "Enabled command suggestions!"
                        }
                        else -> {
                            updateOne(
                                overrides.isSame(),
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
            val overrides = GuildOverrides.getOrCreateOverrides(event.guild.id)
            if (!checkConfigurerRole(overrides))
                return@execute

            GUILD_OVERRIDES.run {
                sendSuccess(
                    when {
                        overrides.noPayRespects -> {
                            updateOne(overrides.isSame(), set(GuildOverrides::noPayRespects, false))
                            "Enabled the pay respects embed!"
                        }
                        else -> {
                            updateOne(overrides.isSame(), set(GuildOverrides::noPayRespects, true))
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
            val overrides = GuildOverrides.getOrCreateOverrides(event.guild.id)
            if (!checkConfigurerRole(overrides))
                return@execute

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

    private suspend fun CommandContext.checkConfigurerRole(overrides: GuildOverrides): Boolean {
        // The administrator permission has absolute power.
        if (event.member!!.hasPermission(Permission.ADMINISTRATOR))
            return true

        if (overrides.canConfigureBot(event.member!!) == false) {
            sendError("You need to have the configurer role")
            return false
        }

        if (overrides.risakoConfigurerRole == null) {
            sendError("Please set a configurer role with `..configrole <role name or role id>`!")
            return false
        }
        return true
    }
}
