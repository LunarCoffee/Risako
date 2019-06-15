@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.listeners

import dev.lunarcoffee.risako.bot.consts.*
import dev.lunarcoffee.risako.bot.std.GuildOverrides
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.extensions.await
import dev.lunarcoffee.risako.framework.core.annotations.ListenerGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import kotlinx.coroutines.*
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.litote.kmongo.eq
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.schedule

@ListenerGroup
internal class PayRespectsListeners(
    private val bot: Bot
) : CoroutineScope by CoroutineScope(Dispatchers.Default), ListenerAdapter() {

    // Pay respects embeds which can still be reacted to (message ID to message object).
    private val active = ConcurrentHashMap<String, Message>()

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val messageIsNotF = !event.message.contentRaw.equals("f", ignoreCase = true)
        val guildNoPayRespects = runBlocking {
            GUILD_OVERRIDES.findOne(GuildOverrides::guildId eq event.guild.id)?.noPayRespects
        } ?: false

        // The option to toggle the embed is provided by the <..togglef> command.
        if (event.author.isBot || messageIsNotF || guildNoPayRespects) {
            return
        }

        event.message.delete().queue()
        launch {
            val message = event.channel.sendMessage(
                embed {
                    title = "${Emoji.INDICATOR_F}  Press **F** to pay respects."
                    description = "**${event.author.asTag}** has paid their respects."
                }
            ).await()

            // Add an emoji to start the chain.
            message.addReaction(Emoji.INDICATOR_F).queue()

            // Register the message and listen for reactions to it for one day.
            active[message.id] = message
            DEFAULT_TIMER.schedule(86_400_000L) { active -= message.id }
        }
    }

    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        val emote = event.reactionEmote
        if (event.user.isBot || emote.isEmoji && emote.emoji != Emoji.INDICATOR_F) {
            return
        }

        val message = active[event.messageId] ?: return
        val prev = message.embeds[0].description!!

        // Return if the user already paid their respects.
        if (event.user.asTag in prev) {
            return
        }

        event.reaction.removeReaction(event.user).queue()
        launch {
            val newMessage = message.editMessage(
                embed {
                    title = "${Emoji.INDICATOR_F}  Press **F** to pay respects."
                    description = "$prev\n**${event.user.asTag}** has paid their respects."
                }
            ).await()
            active[event.messageId] = newMessage
        }
    }
}
