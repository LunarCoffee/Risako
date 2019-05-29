@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.commands.info

import dev.lunarcoffee.risako.bot.constToEng
import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.bot.consts.TIME_FORMATTER
import dev.lunarcoffee.risako.bot.toYesNo
import dev.lunarcoffee.risako.framework.api.dsl.*
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.api.extensions.sendError
import dev.lunarcoffee.risako.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.commands.transformers.TrUser
import dev.lunarcoffee.risako.framework.core.commands.transformers.TrWord
import dev.lunarcoffee.risako.framework.core.std.*
import net.dv8tion.jda.api.entities.*

@CommandGroup("Info")
internal class InfoCommands(private val bot: Bot) {
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
            val user = when (val result = args.get<OpResult<User?>>(0)) {
                is OpSuccess -> result.result ?: event.author
                else -> {
                    sendError("I can't find that user!")
                    return@execute
                }
            }

            send(
                embed {
                    user.run {
                        val botOrUser = if (isBot) "bot" else "user"

                        title = "${Emoji.MAG_GLASS}  Info on $botOrUser **$asTag**:"
                        description = """
                            |**User ID**: $id
                            |**Creation time**: ${timeCreated.format(TIME_FORMATTER)}
                            |**Avatar ID**: ${avatarId ?: "(none)"}
                            |**Mention**: $asMention
                        """.trimMargin()

                        thumbnail { url = avatarUrl ?: defaultAvatarUrl }
                    }
                }
            )
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
            val member = when (val result = args.get<OpResult<User?>>(0)) {
                is OpSuccess -> event.guild.getMember(result.result ?: event.author)
                else -> {
                    sendError("I can't find that user!")
                    return@execute
                }
            }

            if (member == null) {
                sendError("That user is not a member of this server!")
                return@execute
            }

            send(
                embed {
                    member.run {
                        val botOrMember = if (user.isBot) "bot" else "member"
                        val activity = activities.firstOrNull()?.name ?: "(none)"

                        val userRoles = if (roles.isNotEmpty()) {
                            "[${roles.joinToString { it.asMention }}]"
                        } else {
                            "(none)"
                        }

                        title = "${Emoji.MAG_GLASS}  Info on $botOrMember **${user.asTag}**:"
                        description = """
                            |**User ID**: $id
                            |**Nickname**: ${nickname ?: "(none)"}
                            |**Status**: ${onlineStatus.key}
                            |**Activity**: $activity
                            |**Creation time**: ${timeCreated.format(TIME_FORMATTER)}
                            |**Join time**: ${timeJoined.format(TIME_FORMATTER)}
                            |**Avatar ID**: ${user.avatarId ?: "(none)"}
                            |**Mention**: $asMention
                            |**Roles**: ${userRoles.ifEmpty { "(none)" }}
                        """.trimMargin()

                        thumbnail { url = user.avatarUrl ?: user.defaultAvatarUrl }
                    }
                }
            )
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

            val channel = if (nameOrId.toLongOrNull() != null) {
                jda.getTextChannelById(nameOrId) ?: jda.getVoiceChannelById(nameOrId)
            } else {
                jda.getTextChannelsByName(nameOrId, true).firstOrNull()
                    ?: jda.getVoiceChannelByName(nameOrId, false).firstOrNull()
            }

            if (channel == null) {
                sendError("I can't find a text or voice channel with that name or ID!")
                return@execute
            }

            send(
                embed {
                    channel.run {
                        when (this) {
                            is TextChannel -> {
                                val slowmode = if (slowmode != 0) {
                                    SplitTime(slowmode.toLong()).toString()
                                } else {
                                    "(none)"
                                }

                                title = "${Emoji.MAG_GLASS}  Info on text channel **#$name**:"
                                description = """
                                    |**Channel ID**: $id
                                    |**Server**: ${guild.name}
                                    |**Topic**: ${topic ?: "(none)"}
                                    |**Slowmode**: $slowmode
                                    |**NSFW**: ${isNSFW.toYesNo()}
                                    |**Mention**: $asMention
                                    |**Category**: ${parent?.name ?: "(none)"}
                                    |**Creation time**: ${timeCreated.format(TIME_FORMATTER)}
                                """.trimMargin()
                            }
                            is VoiceChannel -> {
                                val limit = if (userLimit == 0) "(none)" else userLimit.toString()

                                title = "${Emoji.MAG_GLASS}  Info on voice channel **#$name**:"
                                description = """
                                    |**Channel ID**: $id
                                    |**Server**: ${guild.name}
                                    |**Bitrate**: ${bitrate / 1_000}kb/s
                                    |**User limit**: $limit users
                                    |**Mention**: <#$id>
                                    |**Category**: ${parent?.name ?: "(none)"}
                                    |**Creation time**: ${timeCreated.format(TIME_FORMATTER)}
                                """.trimMargin()
                            }
                        }
                    }
                }
            )
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

            send(
                embed {
                    emote.run {
                        val animated = if (isAnimated) " animated" else ""

                        title = "${Emoji.MAG_GLASS}  Info on$animated emote **$name**:"
                        description = """
                            |**Emote ID**: $id
                            |**Server** ${guild?.name ?: "(none)"}
                            |**Managed**: ${isManaged.toYesNo()}
                            |**Creation time**: ${timeCreated.format(TIME_FORMATTER)}
                            |**Required roles**: ${roles.ifEmpty { "(none)" }}
                        """.trimMargin()

                        thumbnail { url = imageUrl }
                    }
                }
            )
        }
    }

    fun ri() = command("ri") {
        description = "Gets info about a role."
        aliases = arrayOf("roleinfo")

        extDescription = """
            |`$name [name|id]`\n
            |Gets detailed information about a role. If a name or ID is specified, I will try to
            |get a role with them. Note that even if the ID you provide is valid, I still need to
            |be in the server the role is from in order to get information from it. If a namr or ID
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

            send(
                embed {
                    role.run {
                        val roleName = if (role.isPublicRole) {
                            "the public role"
                        } else {
                            "role **@$name**"
                        }
                        val authorGuildId = event.guild.id
                        val mention = if (guild.id == authorGuildId) asMention else "(unavailable)"
                        val permissions = permissions.map { it.constToEng() }.ifEmpty { "(none)" }

                        title = "${Emoji.MAG_GLASS}  Info on $roleName:"
                        description = """
                            |**Role ID**: $id
                            |**Server**: ${guild.name}
                            |**Displayed separately**: ${isHoisted.toYesNo()}
                            |**Normally mentionable**: ${isMentionable.toYesNo()}
                            |**Mention**: $mention
                            |**Creation time**: ${timeCreated.format(TIME_FORMATTER)}
                            |**Managed**: ${isManaged.toYesNo()}
                            |**Permissions**: $permissions
                        """.trimMargin()
                    }
                }
            )
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
            val guild = if (nameOrId.toLongOrNull() != null) {
                jda.getGuildById(nameOrId)
            } else {
                jda.getGuildsByName(nameOrId, true).firstOrNull()
            }

            if (guild == null) {
                sendError("I can't find a server with that name or ID!")
                return@execute
            }

            send(
                embedPaginator(event.author) {
                    guild.run {
                        page(
                            embed {
                                val afkChannel = afkChannel?.id?.run { "<#$this>" } ?: "(none)"
                                val features = features.map { it.constToEng() }

                                title = "${Emoji.MAG_GLASS}  Info on server **$name**:"
                                description = """
                                    |**Guild ID**: $id
                                    |**Total members**: ${members.size} members
                                    |**Total emotes**: ${emotes.size} emotes
                                    |**Total channels**: ${channels.size} channels
                                    |**Text channels**: ${textChannels.size} text channels
                                    |**Voice channels**: ${voiceChannels.size} voice channels
                                    |**AFK channel**: $afkChannel
                                    |**NSFW filter:** ${explicitContentLevel.description}
                                    |**Special features**: ${features.ifEmpty { "(none)" }}
                                """.trimMargin()

                                thumbnail { url = iconUrl }
                            }
                        )
                        page(
                            embed {
                                val roles = if (guild.id == event.guild.id) {
                                    roles.map { it.asMention }.toString()
                                } else {
                                    "(unavailable)"
                                }

                                title = "${Emoji.MAG_GLASS}  Info on server **$name**:"
                                description = """
                                    |**Owner**: ${owner?.user?.asTag ?: "(none)"}
                                    |**Voice region**: ${region.getName()}
                                    |**Roles**: ${roles.ifEmpty { "(none)" }}
                                    |**Verification level**: ${verificationLevel.constToEng()}
                                    |**MFA level**: ${requiredMFALevel.constToEng()}
                                    |**Icon ID**: ${iconId ?: "(no icon)"}
                                """.trimMargin()

                                thumbnail { url = iconUrl }
                            }
                        )
                    }
                }
            )
        }
    }
}
