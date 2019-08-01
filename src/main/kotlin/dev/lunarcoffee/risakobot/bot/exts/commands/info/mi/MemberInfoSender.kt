package dev.lunarcoffee.risakobot.bot.exts.commands.info.mi

import dev.lunarcoffee.risakobot.bot.consts.Emoji
import dev.lunarcoffee.risakobot.bot.consts.TIME_FORMATTER
import dev.lunarcoffee.risakobot.framework.api.dsl.embed
import dev.lunarcoffee.risakobot.framework.api.extensions.send
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import dev.lunarcoffee.risakobot.framework.core.std.ContentSender
import net.dv8tion.jda.api.entities.Member

internal class MemberInfoSender(private val member: Member) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.send(
            embed {
                member.run {
                    val botOrMember = if (user.isBot) "" else "member"
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

                    thumbnail { url = user.effectiveAvatarUrl }
                }
            }
        )
    }
}
