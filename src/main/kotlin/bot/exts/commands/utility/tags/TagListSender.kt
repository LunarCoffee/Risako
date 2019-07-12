package bot.exts.commands.utility.tags

import bot.consts.ColName
import bot.consts.Emoji
import framework.api.dsl.embed
import framework.api.dsl.embedPaginator
import framework.api.extensions.send
import framework.api.extensions.sendSuccess
import framework.core.DB
import framework.core.commands.CommandContext
import framework.core.std.ContentSender
import framework.core.std.SplitTime
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
