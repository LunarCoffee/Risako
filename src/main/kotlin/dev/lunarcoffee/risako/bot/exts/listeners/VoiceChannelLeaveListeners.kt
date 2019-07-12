@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.listeners

import dev.lunarcoffee.risako.framework.core.annotations.ListenerGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import kotlinx.coroutines.*
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

@ListenerGroup
internal class VoiceChannelLeaveListeners(private val bot: Bot) : ListenerAdapter() {
    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        if (
            event.channelLeft.members.size == 1 &&
            event.channelLeft.members[0].id == bot.jda.selfUser.id
        ) {
            GlobalScope.launch {
                // Leave if no one has joined in 5 seconds.
                delay(5_000)
                event.guild.audioManager.run {
                    if (
                        isConnected &&
                        connectedChannel!!.members.size == 1 &&
                        connectedChannel!!.members[0].id == bot.jda.selfUser.id
                    ) {
                        closeAudioConnection()
                    }
                }
            }
        }
    }
}
