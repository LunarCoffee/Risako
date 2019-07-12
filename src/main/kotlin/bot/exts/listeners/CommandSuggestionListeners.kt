@file:Suppress("unused")

package bot.exts.listeners

import bot.consts.GUILD_OVERRIDES
import bot.std.GuildOverrides
import framework.api.extensions.await
import framework.api.extensions.sendError
import framework.core.annotations.ListenerGroup
import framework.core.bot.Bot
import kotlinx.coroutines.*
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.litote.kmongo.eq
import kotlin.math.min

@ListenerGroup
internal class CommandSuggestionListeners(
    private val bot: Bot
) : CoroutineScope by CoroutineScope(Dispatchers.Default), ListenerAdapter() {

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val notRisakoCommand = !event.message.contentRaw.startsWith(bot.config.prefix)
        val noCommandSuggestions = runBlocking {
            GUILD_OVERRIDES.findOne(GuildOverrides::guildId eq event.guild.id)?.noSuggestCommands
        } ?: false

        if (notRisakoCommand || event.author.isBot || noCommandSuggestions) {
            return
        }

        val name = event.message.contentRaw.substringAfter(bot.config.prefix).trim()
        if (name !in bot.commandNames) {
            suggestCommandNames(event.channel, name)
        }
    }

    private fun suggestCommandNames(channel: TextChannel, name: String) {
        // Don't do anything if the user sent only the prefix.
        if (name.isBlank()) {
            return
        }

        for (alias in bot.commandNames) {
            if (nameDistance(name, alias) < 2) {
                launch {
                    channel.sendError("That's not a command... did you mean `$alias`?") {
                        delay(5000L)
                        it.delete().await()
                    }
                }
                return
            }
        }
    }

    private fun nameDistance(first: String, second: String): Int {
        val prev = IntArray(second.length + 1) { it }
        val cur = IntArray(second.length + 1)
        var cost: Int

        for (i in 0 until first.length) {
            cur[0] = i + 1
            for (j in 0 until second.length) {
                cost = if (first[i] == second[j]) 0 else 1
                cur[j + 1] = min(cur[j] + 1, min(prev[j + 1] + 1, prev[j] + cost))
            }
            cur.copyInto(prev)
        }
        return cur[second.length]
    }
}
