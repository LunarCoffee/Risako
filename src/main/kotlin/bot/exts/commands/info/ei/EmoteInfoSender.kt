package bot.exts.commands.info.ei

import bot.consts.Emoji
import bot.consts.TIME_FORMATTER
import bot.toYesNo
import framework.api.dsl.embed
import framework.api.extensions.send
import framework.core.commands.CommandContext
import framework.core.std.ContentSender
import net.dv8tion.jda.api.entities.Emote

internal class EmoteInfoSender(private val emote: Emote) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.send(
            embed {
                emote.run {
                    val animated = if (isAnimated) " animated" else ""

                    title = "${Emoji.MAG_GLASS}  Info on$animated emote **$name**:"
                    description = """
                        |**Emote ID**: $id
                        |**Server**: ${guild?.name ?: "(none)"}
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
