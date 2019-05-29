package dev.lunarcoffee.risako.bot.exts.commands.utility.tags

import dev.lunarcoffee.risako.bot.consts.ColName
import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.dsl.embedPaginator
import dev.lunarcoffee.risako.framework.api.extensions.*
import dev.lunarcoffee.risako.framework.core.DB
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.SplitTime
import org.litote.kmongo.and
import org.litote.kmongo.eq
import java.util.*

internal class TagManager(private val ctx: CommandContext) {
    suspend fun sendTags() {
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

    suspend fun sendSingleTag(name: String) {
        val tag = col.findOne(sameTag(name))
        if (tag == null) {
            ctx.sendError("There is no tag with that name!")
            return
        }

        ctx.send(
            embed {
                tag.run {
                    val authorTag = ctx.jda.getUserById(authorId)?.asTag ?: "(none)"
                    val timeMs = timeCreated.toInstant().toEpochMilli()
                    val time = SplitTime(timeMs - System.currentTimeMillis())
                        .localWithoutWeekday()

                    title = "${Emoji.BOOKMARK}  Tag **${this@run.name}**:"
                    description = """
                        |**Author**: $authorTag
                        |**Time created**: $time
                    """.trimMargin()

                    field {
                        this@field.name = "Content:"
                        content = this@run.content.removeSuffix(attachments)
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

    suspend fun addTag(name: String, content: String) {
        if (name.length > 30 || content.length > 1_000) {
            ctx.sendError("The name or content of your tag is too long!")
            return
        }

        // Don't allow duplicates in the same guild.
        if (col.findOne(sameTag(name)) != null) {
            ctx.sendError("A tag with that name already exists!")
            return
        }

        // Add attachments to the tag as well.
        val attachments = ctx.event.message.attachments.joinToString("\n") { it.url }
        val fullContent = "$content\n$attachments"

        col.insertOne(Tag(ctx.event.guild.id, ctx.event.author.id, name, fullContent, Date()))
        ctx.sendSuccess("Your tag has been created!")
    }

    suspend fun editTag(name: String, content: String) {
        val tag = col.findOne(sameTag(name))
        if (tag == null) {
            ctx.sendError("There is no tag with that name!")
            return
        }

        // Only let people edit their own tags.
        if (tag.authorId != ctx.event.author.id) {
            ctx.sendError("You can only edit your own tags!")
            return
        }

        col.updateOne(sameTag(name), tag.apply { this@apply.content = "$content\n$attachments" })
        ctx.sendSuccess("Your tag has been edited!")
    }

    suspend fun deleteTag(name: String) {
        val tag = col.findOne(sameTag(name))
        if (tag == null) {
            ctx.sendError("There is no tag with that name!")
            return
        }

        // Only let people delete their own tags.
        if (tag.authorId != ctx.event.author.id) {
            ctx.sendError("You can only delete your own tags!")
            return
        }

        col.deleteOne(sameTag(name))
        ctx.sendSuccess("Your tag has been deleted!")
    }

    // Matches tags by guild ID and name.
    private fun sameTag(name: String) = and(Tag::guildId eq ctx.event.guild.id, Tag::name eq name)

    companion object {
        private val col = DB.getCollection<Tag>(ColName.TAGS)
    }
}
