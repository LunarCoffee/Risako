package dev.lunarcoffee.risako.bot.exts.commands.utility.tags

import dev.lunarcoffee.risako.bot.consts.ColName
import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.api.extensions.sendError
import dev.lunarcoffee.risako.framework.core.DB
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.ContentSender
import dev.lunarcoffee.risako.framework.core.std.SplitTime
import org.litote.kmongo.and
import org.litote.kmongo.eq

internal class SingleTagSender(
    private val name: String,
    private val raw: Boolean
) : ContentSender {

    override suspend fun send(ctx: CommandContext) {
        val tag = col.findOne(and(Tag::guildId eq ctx.event.guild.id, Tag::name eq name))
        if (tag == null) {
            ctx.sendError("There is no tag with that name!")
            return
        }

        if (raw) {
            ctx.send(tag.content)
            return
        }

        ctx.send(
            embed {
                tag.run {
                    val authorTag = ctx.jda.getUserById(authorId)?.asTag ?: "(none)"
                    val timeMs = timeCreated.toInstant().toEpochMilli()
                    val time = SplitTime(timeMs - System.currentTimeMillis()).localWithoutWeekday()

                    title = "${Emoji.BOOKMARK}  Tag **${this@run.name}**:"
                    description = """
                        |**Author**: $authorTag
                        |**Time created**: $time
                    """.trimMargin()

                    if (textContent.isNotBlank()) {
                        field {
                            this@field.name = "Content:"
                            content = textContent
                        }
                    }

                    if (attachments.isNotEmpty()) {
                        field {
                            this@field.name = "Attachments:"
                            content = namedAttachments
                        }
                    }
                }
            }
        )
    }

    companion object {
        private val col = DB.getCollection<Tag>(ColName.TAGS)
    }
}
