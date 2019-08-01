@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.commands.mod

import dev.lunarcoffee.risako.bot.exts.commands.mod.ban.BanController
import dev.lunarcoffee.risako.bot.exts.commands.mod.kick.KickController
import dev.lunarcoffee.risako.bot.exts.commands.mod.logs.AuditLogSender
import dev.lunarcoffee.risako.bot.exts.commands.mod.mute.MuteController
import dev.lunarcoffee.risako.bot.exts.commands.mod.mutel.MuteDetailsSender
import dev.lunarcoffee.risako.bot.exts.commands.mod.mutel.MuteListSender
import dev.lunarcoffee.risako.bot.exts.commands.mod.purge.ChannelPurger
import dev.lunarcoffee.risako.framework.api.dsl.command
import dev.lunarcoffee.risako.framework.api.extensions.*
import dev.lunarcoffee.risako.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.commands.transformers.*
import dev.lunarcoffee.risako.framework.core.std.SplitTime
import dev.lunarcoffee.risako.framework.core.std.UserNotFound
import net.dv8tion.jda.api.Permission
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
            |from this dev.lunarcoffee.risako.bot. The unmuted user will be sent a message with the person who unmuted
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
        aliases = arrayOf("mutelist", "silencel", "silencelist", "softbanl", "softbanlist")

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
                send(MuteDetailsSender(user))
                return@execute
            }
            send(MuteListSender())
        }
    }

    fun kick() = command("kick") {
        description = "Kicks a member from the current server."
        extDescription = """
            |`$name user [reason]`\n
            |Kicks a user from the current server. I must be have the permission to kick members.
            |When a member is kicked, they will be sent a message with `reason` (or `(no reason)`)
            |if no reason is specified and the user that kicked them. You must be able to kick
            |members to use this command.
        """

        expectedArgs = arrayOf(TrUser(), TrRest(true, "(no reason)"))
        execute { args ->
            val user = args.get<User>(0)
            if (user is UserNotFound) {
                sendError("I can't find that user!")
                return@execute
            }
            val reason = args.get<String>(1)
            KickController(this).kick(user, reason)
        }
    }

    fun ban() = command("ban") {
        description = "Permanently bans a member from a server."
        extDescription = """
            |`$name user [reason]`\n
            |Bans a user from the current server. I must be have the permission to ban members.
            |When a member is banned, they will be sent a message with `reason` (or `(no reason)`)
            |if no reason is specified and the user that banned them. You must be able to ban
            |members to use this command.
        """

        expectedArgs = arrayOf(TrUser(), TrRest(true, "(no reason)"))
        execute { args ->
            val user = args.get<User>(0)
            if (user is UserNotFound) {
                sendError("I can't find that user!")
                return@execute
            }
            val reason = args.get<String>(1)
            BanController(this).ban(user, reason)
        }
    }

    fun unban() = command("unban") {
        description = "Unbans a member from the current server."
        extDescription = """
            |`$name name|id`\n
            |Unbans a banned user from the current server. I must have the permission to ban
            |members. When a member is unbanned, they might be sent a message with the person who
            |unbanned them. This only happens if I am in a server that they are also in. You must
            |be able to ban members to use this command.
        """

        expectedArgs = arrayOf(TrWord())
        execute { args ->
            val nameOrId = args.get<String>(0)
            val user = event
                .guild
                .retrieveBanList()
                .await()
                .find { nameOrId in arrayOf(it.user.id, it.user.name, it.user.asTag) }
                ?.user
            if (user == null) {
                sendError("Either that user is not banned, or doesn't exist!")
                return@execute
            }
            BanController(this).unban(user)
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
            val user = args.get<User?>(1)
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

            if (!event.guild.getMember(event.author)!!.hasPermission(Permission.MANAGE_CHANNEL)) {
                sendError("You need to be able to manage channels to use this command!")
                return@execute
            }

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

    fun logs() = command("logs") {
        description = "Gets this server's audit log history."
        aliases = arrayOf("audits", "auditlogs")

        extDescription = """
            |`$name [limit]`\n
            |This command retrieves the last `limit` entries in the audit log. If `limit` is not
            |given, I will get the last ten entries. For each audit log entry, I'll show the type
            |of the audit, the user that initiated it, the affected target type and name, the time
            |at which it took place, and the reason (when a user is banned, for example).
            |&{Limitations:}
            |I won't show you what actually changed, since that would require more effort for me to
            |do than for you to open up the audit logs in the server settings. You need to be able
            |to view the logs already to use this command, anyway.
        """

        expectedArgs = arrayOf(TrInt(true, 10))
        execute { args ->
            val limit = args.get<Int>(0)
            if (limit !in 1..100) {
                sendError("I can't get that many log entries!")
                return@execute
            }
            send(AuditLogSender(limit))
        }
    }
}
