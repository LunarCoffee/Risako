@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.commands.info

import dev.lunarcoffee.risako.bot.exts.commands.info.ci.ChannelInfoSender
import dev.lunarcoffee.risako.bot.exts.commands.info.ei.EmoteInfoSender
import dev.lunarcoffee.risako.bot.exts.commands.info.mi.MemberInfoSender
import dev.lunarcoffee.risako.bot.exts.commands.info.ri.RoleInfoSender
import dev.lunarcoffee.risako.bot.exts.commands.info.si.ServerInfoSender
import dev.lunarcoffee.risako.bot.exts.commands.info.ui.UserInfoSender
import dev.lunarcoffee.risako.framework.api.dsl.command
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.api.extensions.sendError
import dev.lunarcoffee.risako.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.commands.transformers.TrUser
import dev.lunarcoffee.risako.framework.core.commands.transformers.TrWord
import dev.lunarcoffee.risako.framework.core.std.UserNotFound
import net.dv8tion.jda.api.entities.User

@CommandGroup("Info")
class InfoCommands(private val bot: Bot) {
    fun ui() = command("ui") {
        description = "Gets info about a user."
        aliases = arrayOf("userinfo")

        extDescription = """
            |`$name [user]`\n
            |Gets basic information about a user. If a name or ID is specified, this command will
            |attempt to fetch a user with them. If not, the author of the message will be used. If
            |the user is in the current server, the `mi` command may provide more detailed info.
        """

        expectedArgs = arrayOf(TrUser(true))
        execute { args ->
            val user = when (val result = args.get<User?>(0)) {
                null -> event.author
                UserNotFound -> {
                    sendError("I can't find that user!")
                    return@execute
                }
                else -> result
            }
            send(UserInfoSender(user))
        }
    }

    fun mi() = command("mi") {
        description = "Gets info about a member of the current server."
        aliases = arrayOf("memberinfo")

        extDescription = """
            |`$name [user]`\n
            |Gets detailed information about a member of the current server. If a name or ID is
            |specified, this command will attempt to fetch a user with them. If not, the author of
            |the message will be used. If the user is not in the current server, the `ui` command
            |may be useful.
        """

        expectedArgs = arrayOf(TrUser(true))
        execute { args ->
            val member = event.guild.getMember(
                when (val result = args.get<User?>(0)) {
                    null -> event.author
                    UserNotFound -> {
                        sendError("I can't find that user!")
                        return@execute
                    }
                    else -> result
                }
            )

            if (member == null) {
                sendError("That user is not a member of this server!")
                return@execute
            }
            send(MemberInfoSender(member))
        }
    }

    fun ci() = command("ci") {
        description = "Gets info about a channel."
        aliases = arrayOf("channelinfo")

        extDescription = """
            |`$name [name|id]`\n
            |Gets detailed information about a text or voice channel. If a name or ID is specified,
            |this command will attempt to get a channel with them. Note that even if the name or ID
            |you give me may be valid, I have to be in the server with that channel in order to get
            |info about it. If you don't give me a name or ID, the current channel will be used.
        """

        expectedArgs = arrayOf(TrWord(true))
        execute { args ->
            val nameOrId = args.get<String>(0)
                .ifEmpty { event.channel.id }
                .replace("""[#<>]""".toRegex(), "")  // Trim channel mention prefix and suffix.

            val channel = if (nameOrId.toLongOrNull() != null)
                jda.getTextChannelById(nameOrId) ?: jda.getVoiceChannelById(nameOrId)
            else
                jda.getTextChannelsByName(nameOrId, true).firstOrNull()
                    ?: jda.getVoiceChannelsByName(nameOrId, false).firstOrNull()

            if (channel == null) {
                sendError("I can't find a text or voice channel with that name or ID!")
                return@execute
            }
            send(ChannelInfoSender(channel))
        }
    }

    fun ei() = command("ei") {
        description = "Gets info about a custom emote."
        aliases = arrayOf("emoteinfo")

        extDescription = """
            |`$name name|id`\n
            |Gets detailed information about a custom emote. You must specify the name or ID of the
            |emote you are looking for, and I will attempt to get it for you. Note that even if an
            |ID you provide is valid, I have to be in the server where it comes from in order to be
            |able to get it.
        """

        expectedArgs = arrayOf(TrWord())
        execute { args ->
            // Trim emote mention characters.
            val nameOrId = args.get<String>(0).replace("""[:<>]""".toRegex(), "")
            val pureId = nameOrId.takeLast(18)

            // Prioritize emotes from the current guild.
            val emote = event.guild.getEmotesByName(nameOrId, true).firstOrNull()
                ?: jda.getEmotesByName(nameOrId, true).firstOrNull()
                ?: if (pureId.toLongOrNull() != null) jda.getEmoteById(pureId) else null

            if (emote == null) {
                sendError("I can't find an emote with that name or ID!")
                return@execute
            }
            send(EmoteInfoSender(emote))
        }
    }

    fun ri() = command("ri") {
        description = "Gets info about a role."
        aliases = arrayOf("roleinfo")

        extDescription = """
            |`$name [name|id]`\n
            |Gets detailed information about a role. If a name or ID is specified, I will try to
            |get a role with them. Note that even if the ID you provide is valid, I still need to
            |be in the server the role is from in order to get information from it. If a name or ID
            |is not specified, I will get information about the default role (shows up as @everyone
            |in the server settings).
        """

        expectedArgs = arrayOf(TrWord(true))
        execute { args ->
            val nameOrId = args.get<String>(0).ifEmpty { event.guild.publicRole.id }

            // Prioritize roles from the current guild.
            val role = event.guild.getRolesByName(nameOrId, true).firstOrNull()
                ?: jda.getRolesByName(nameOrId, true).firstOrNull()
                ?: if (nameOrId.toLongOrNull() != null) jda.getRoleById(nameOrId) else null

            if (role == null) {
                sendError("I can't find a role with that name or ID!")
                return@execute
            }
            send(RoleInfoSender(role))
        }
    }

    fun si() = command("si") {
        description = "Gets info about a server."
        aliases = arrayOf("gi", "guildinfo", "serverinfo")

        extDescription = """
            |`$name [name|id]`\n
            |Gets detailed information about a server. If a name or ID is specified, this command
            |will attempt to get a server with them. Note that even if the name or ID you give is
            |valid, I have to be in the server to be able to find it. If you don't give me a name
            |or ID, the current server will be used instead.
        """

        expectedArgs = arrayOf(TrWord(true))
        execute { args ->
            val nameOrId = args.get<String>(0).ifEmpty { event.guild.id }
            val guild = if (nameOrId.toLongOrNull() != null)
                jda.getGuildById(nameOrId)
            else
                jda.getGuildsByName(nameOrId, true).firstOrNull()

            if (guild == null) {
                sendError("I can't find a server with that name or ID!")
                return@execute
            }
            send(ServerInfoSender(guild))
        }
    }
}
