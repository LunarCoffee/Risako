package dev.lunarcoffee.risako.bot.exts.commands.info.ei

import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.bot.consts.TIME_FORMATTER
import dev.lunarcoffee.risako.bot.toYesNo
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.ContentSender
import net.dv8tion.jda.api.entities.Emote

class EmoteInfoSender(private val emote: Emote) : ContentSender {
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
