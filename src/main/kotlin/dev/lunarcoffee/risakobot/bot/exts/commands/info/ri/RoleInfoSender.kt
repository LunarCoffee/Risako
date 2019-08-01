package dev.lunarcoffee.risakobot.bot.exts.commands.info.ri

import dev.lunarcoffee.risakobot.bot.constToEng
import dev.lunarcoffee.risakobot.bot.consts.Emoji
import dev.lunarcoffee.risakobot.bot.consts.TIME_FORMATTER
import dev.lunarcoffee.risakobot.bot.toYesNo
import dev.lunarcoffee.risakobot.framework.api.dsl.embed
import dev.lunarcoffee.risakobot.framework.api.extensions.send
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import dev.lunarcoffee.risakobot.framework.core.std.ContentSender
import net.dv8tion.jda.api.entities.Role

internal class RoleInfoSender(private val role: Role) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.send(
            embed {
                role.run {
                    val roleName = if (role.isPublicRole) {
                        "the public role"
                    } else {
                        "role **@$name**"
                    }
                    val authorGuildId = ctx.event.guild.id
                    val mention = if (guild.id == authorGuildId) asMention else "(unavailable)"
                    val peopleWith = guild.members.map { it.roles }.count { this in it }
                    val permissions = permissions.map { it.constToEng() }.ifEmpty { "(none)" }

                    title = "${Emoji.MAG_GLASS}  Info on $roleName:"
                    description = """
                        |**Role ID**: $id
                        |**Server**: ${guild.name}
                        |**Displayed separately**: ${isHoisted.toYesNo()}
                        |**Normally mentionable**: ${isMentionable.toYesNo()}
                        |**Mention**: $mention
                        |**Members with role**: $peopleWith members
                        |**Creation time**: ${timeCreated.format(TIME_FORMATTER)}
                        |**Managed**: ${isManaged.toYesNo()}
                        |**Permissions**: $permissions
                    """.trimMargin()
                }
            }
        )
    }
}