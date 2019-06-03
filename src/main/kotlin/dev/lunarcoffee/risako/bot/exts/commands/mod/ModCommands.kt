@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.commands.mod

import dev.lunarcoffee.risako.bot.exts.commands.mod.mute.MuteController
import dev.lunarcoffee.risako.bot.exts.commands.mod.mutel.MuteDetailsSender
import dev.lunarcoffee.risako.bot.exts.commands.mod.mutel.MuteListSender
import dev.lunarcoffee.risako.bot.exts.commands.mod.purge.ChannelPurger
import dev.lunarcoffee.risako.framework.api.dsl.command
import dev.lunarcoffee.risako.framework.api.extensions.sendError
import dev.lunarcoffee.risako.framework.api.extensions.sendSuccess
import dev.lunarcoffee.risako.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.commands.transformers.*
import dev.lunarcoffee.risako.framework.core.std.SplitTime
import dev.lunarcoffee.risako.framework.core.std.UserNotFound
import net.dv8tion.jda.api.entities.User

@CommandGroup("Mod")
internal class ModCommands(private val bot: Bot) {
    fun mute() = command("mute") {
        description = "Mutes a member for a specified amount of time."
        aliases = arrayOf("silence", "softban")

        extDescription = """
            |`$name user time [reason]`\n
            |Mutes a user for a specified amount of time. I must have the permission to manage
            |roles. When a member is muted, they will be sent a message with `time` in a readable
            |format, the provided `reason` (or `(no reason)`) if none is provided, and the user
            |that muted them. You must be able to manage roles to use this command.
        """

        expectedArgs = arrayOf(TrUser(), TrTime(), TrRest(true, "(no reason)"))
        execute { args ->
            val user = args.get<User>(0)
            if (user is UserNotFound) {
                sendError("I can't find that user!")
                return@execute
            }

            val time = args.get<SplitTime>(1)
            val reason = args.get<String>(2)
            MuteController(this).mute(user, time, reason)
        }
    }

    fun unmute() = command("unmute") {
        description = "Unmutes a currently muted member."
        aliases = arrayOf("unsilence", "unsoftban")

        extDescription = """
            |`$name user`\n
            |Unmutes a muted user. This only works if the user was muted with the `..mute` command
            |from this bot. The unmuted user will be sent a message with the person who unmuted
            |them. You must be able to manage roles to use this command.
        """

        expectedArgs = arrayOf(TrUser())
        execute { args ->
            val user = args.get<User>(0)
            MuteController(this).unmute(user)
        }
    }

    fun mutel() = command("mutel") {
        description = "Shows the muted members on the current server."
        aliases = arrayOf("silencelist", "softbanlist")

        extDescription = """
            |`$name [user]`\n
            |Without arguments, this command lists all muted members of the current server, along
            |with the remaining time they will be muted for (without a manual unmute). When `user`
            |is provided, this command lists details about their mute, including the reason, the
            |remaining time, and their previous roles.
        """

        expectedArgs = arrayOf(TrUser(true))
        execute { args ->
            val user = args.get<User?>(0)
            if (user is UserNotFound) {
                sendError("I can't find that user!")
                return@execute
            }

            if (user != null) {
                MuteDetailsSender(user).send(this)
                return@execute
            }
            MuteListSender().send(this)
        }
    }

    fun purge() = command("purge") {
        description = "Deletes a certain amount of messages from a channel."
        aliases = arrayOf("clear", "massdelete")

        extDescription = """
            |`$name limit [user]`\n
            |Deletes the past `limit` messages from the current channel, the message containing the
            |command exempt. If `user` is specified, this command deletes the past `limit` messages
            |from only that user. You must be able to manage messages to use this command.
        """

        expectedArgs = arrayOf(TrInt(), TrUser(true))
        execute { args ->
            val limit = args.get<Int>(0)
            val user = args.get<User?>(0)
            val purger = ChannelPurger(this, limit)

            when (user) {
                is UserNotFound -> {
                    sendError("I can't find that user!")
                    return@execute
                }
                null -> purger.purgeAll()
                else -> purger.purgeFromUser(user, user == event.author)
            }
        }
    }

    fun slow() = command("slow") {
        description = "Sets the current text channel's slowmode."
        aliases = arrayOf("cooldown", "slowmode")

        extDescription = """
            |`$name time`
            |When setting the slowmode cooldown of a channel in the Discord client's channel
            |settings, the only options available are at fixed lengths of time. This command lets
            |you change it to any arbitrary time between none to six hours. The `time` argument
            |should look something like `2m 30s`, `1h`, or `0s`, to give some examples.
        """

        expectedArgs = arrayOf(TrTime())
        execute { args ->
            val slowmode = args.get<SplitTime>(0)
            val slowmodeSeconds = slowmode.totalMs.toInt() / 1_000

            if (slowmodeSeconds !in 0..21_600) {
                sendError("I can't set this channel's slowmode to that amount of time!")
                return@execute
            }

            val channel = event.guild.getTextChannelById(event.channel.id)!!
            channel.manager.setSlowmode(slowmodeSeconds).queue()

            val slowmodeRepr = if (slowmode.totalMs > 0) "`$slowmode`" else "disabled"
            sendSuccess("This channel's slowmode time is now $slowmodeRepr!")
        }
    }
}
