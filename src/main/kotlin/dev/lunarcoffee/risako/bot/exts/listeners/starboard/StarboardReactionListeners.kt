@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.listeners.starboard

import dev.lunarcoffee.risako.bot.consts.*
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.dsl.message
import dev.lunarcoffee.risako.framework.api.extensions.await
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.core.DB
import dev.lunarcoffee.risako.framework.core.annotations.ListenerGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import kotlinx.coroutines.*
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.guild.react.*
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.litote.kmongo.eq

@ListenerGroup
class StarboardReactionListeners(
    private val bot: Bot
) : CoroutineScope by CoroutineScope(Dispatchers.IO), ListenerAdapter() {

    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        if (isNotStarEmoji(event))
            return

        val starboardChannel = getStarboardChannel(event) ?: return
        val message = getMessageFromEvent(event)

        launch {
            // Check if we have already stored this message.
            val existingEntry = col.findOne(StarboardEntry::messageId eq message.id)

            if (existingEntry != null) {
                // Get the existing embed and update it.
                val existing = starboardChannel
                    .retrieveMessageById(existingEntry.entryMessageId)
                    .await()

                existing.editMessage(message { embed = getStarboardEmbed(message, event) })
                    .await()
            } else {
                val messageId = starboardChannel.send(getStarboardEmbed(message, event)).id

                // Add the new entry to the database.
                val entry = StarboardEntry(messageId, message.id)
                col.insertOne(entry)
            }
        }
    }

    override fun onGuildMessageReactionRemove(event: GuildMessageReactionRemoveEvent) {
        if (isNotStarEmoji(event))
            return

        val starboardChannel = getStarboardChannel(event) ?: return
        val message = getMessageFromEvent(event)

        launch {
            // Check if we have already stored this message.
            val existingEntry = col.findOne(StarboardEntry::messageId eq message.id)!!

            // Get the existing embed.
            val existing = starboardChannel
                .retrieveMessageById(existingEntry.entryMessageId)
                .await()

            // Remove the embed and database entry if there are no more stars.
            if (getStarCount(event) == 0) {
                existing.delete().await()
                col.deleteOne(StarboardEntry::messageId eq existing.id)
                return@launch
            }

            existing.editMessage(message { embed = getStarboardEmbed(message, event) }).await()
        }
    }

    private fun isNotStarEmoji(event: GenericGuildMessageReactionEvent): Boolean {
        val emote = event.reactionEmote
        return !emote.isEmoji || emote.isEmoji && emote.emoji != Emoji.STAR
    }

    private fun getStarboardChannel(event: GenericGuildMessageReactionEvent): TextChannel? {
        return event.guild.textChannels.find { "starboard" in it.name }
    }

    private fun getMessageFromEvent(event: GenericGuildMessageReactionEvent): Message {
        return runBlocking { event.channel.retrieveMessageById(event.messageId).await() }
    }

    private fun getStarboardEmbed(
        message: Message,
        event: GenericGuildMessageReactionEvent
    ): MessageEmbed {

        return embed {
            if (message.contentRaw.isNotBlank()) {
                field {
                    name = "Content"
                    content = message.contentRaw
                }
            }

            field {
                name = "Information"
                content = """
                    |**Author**: ${message.author.asMention}
                    |**Channel**: ${event.channel.asMention}
                    |**Send time**: ${message.timeCreated.format(TIME_FORMATTER)}
                    |**Link**: [click here](${message.jumpUrl})
                """.trimMargin()
            }

            // Show the first image if it exists.
            val image = message.attachments.firstOrNull { it.isImage }
            if (image != null && image.isImage) {
                image {
                    url = image.url
                    proxyUrl = image.proxyUrl
                }
            }

            // Show star count in the footer with proper grammar.
            footer {
                val count = getStarCount(event)
                val plural = if (count != 1) "s" else ""

                text = "${Emoji.STAR} $count star${plural}!"
            }
        }
    }

    private fun getStarCount(event: GenericGuildMessageReactionEvent): Int {
        return runBlocking {
            event
                .channel
                .retrieveMessageById(event.messageId)
                .await()
                .reactions
                .find { it.reactionEmote == event.reactionEmote }
                ?.count ?: 0
        }
    }

    companion object {
        private val col = DB.getCollection<StarboardEntry>(ColName.STARBOARD)
    }
}