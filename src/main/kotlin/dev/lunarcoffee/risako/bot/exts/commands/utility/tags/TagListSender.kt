package dev.lunarcoffee.risako.bot.exts.commands.utility.tags

import dev.lunarcoffee.risako.bot.consts.ColName
import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.dsl.embedPaginator
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.api.extensions.sendSuccess
import dev.lunarcoffee.risako.framework.core.DB
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.ContentSender
import dev.lunarcoffee.risako.framework.core.std.SplitTime
import org.litote.kmongo.eq

internal class TagListSender : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        // Sort tag entries by the time they were created at.
        val tags = col
            .find(Tag::guildId eq ctx.event.guild.id)
            .toList()
            .sortedByDescending { it.timeCreated }
            .chunked(16)

        if (tags.isEmpty()) {
            ctx.sendSuccess("There are no tags in this server!")
            return
        }

        ctx.send(
            embedPaginator(ctx.event.author) {
                for (tagPage in tags) {
                    page(
                        embed {
                            title = "${Emoji.BOOKMARK}  Tags in this server:"
                            description = tagPage.joinToString("\n") {
                                val timeMs = it.timeCreated.toInstant().toEpochMilli()
                                val time = SplitTime(timeMs - System.currentTimeMillis())
                                    .localWithoutWeekday()

                                "**${it.name}**: $time"
                            }
                        }
                    )
                }
            }
        )
    }

    companion object {
        private val col = DB.getCollection<Tag>(ColName.TAGS)
    }
}
