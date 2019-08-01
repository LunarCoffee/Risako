package dev.lunarcoffee.risakobot.bot.exts.commands.info.ui

import dev.lunarcoffee.risakobot.bot.consts.Emoji
import dev.lunarcoffee.risakobot.bot.consts.TIME_FORMATTER
import dev.lunarcoffee.risakobot.framework.api.dsl.embed
import dev.lunarcoffee.risakobot.framework.api.extensions.send
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import dev.lunarcoffee.risakobot.framework.core.std.ContentSender
import net.dv8tion.jda.api.entities.User

internal class UserInfoSender(val user: User) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.send(
            embed {
                user.run {
                    val botOrUser = if (isBot) "" else "user"

                    title = "${Emoji.MAG_GLASS}  Info on $botOrUser **$asTag**:"
                    description = """
                        |**User ID**: $id
                        |**Creation time**: ${timeCreated.format(TIME_FORMATTER)}
                        |**Avatar ID**: ${avatarId ?: "(none)"}
                        |**Mention**: $asMention
                    """.trimMargin()

                    thumbnail { url = effectiveAvatarUrl }
                }
            }
        )
    }
}
