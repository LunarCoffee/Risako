package dev.lunarcoffee.risakobot.bot.exts.commands.utility.tags

import dev.lunarcoffee.risakobot.bot.consts.ColName
import dev.lunarcoffee.risakobot.framework.api.extensions.sendError
import dev.lunarcoffee.risakobot.framework.api.extensions.sendSuccess
import dev.lunarcoffee.risakobot.framework.core.DB
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import org.litote.kmongo.and
import org.litote.kmongo.eq
import java.util.*

internal class TagManager(private val ctx: CommandContext) {
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
