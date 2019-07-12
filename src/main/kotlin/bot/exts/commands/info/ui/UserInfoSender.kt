package bot.exts.commands.info.ui

import bot.consts.Emoji
import bot.consts.TIME_FORMATTER
import framework.api.dsl.embed
import framework.api.extensions.send
import framework.core.commands.CommandContext
import framework.core.std.ContentSender
import net.dv8tion.jda.api.entities.User

internal class UserInfoSender(val user: User) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.send(
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

                    thumbnail { url = effectiveAvatarUrl }
                }
            }
        )
    }
}
