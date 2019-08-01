@file:Suppress("unused")

package dev.lunarcoffee.risakobot.bot.exts.listeners

import dev.lunarcoffee.risakobot.bot.consts.Emoji
import dev.lunarcoffee.risakobot.framework.api.extensions.sendSuccess
import dev.lunarcoffee.risakobot.framework.core.annotations.ListenerGroup
import dev.lunarcoffee.risakobot.framework.core.bot.Bot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

@ListenerGroup
internal class MentionListeners(private val bot: Bot) : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val content = event.message.contentRaw

        // React to the message if it contained a mention to the owner or the bot.
        if ("<@${bot.config.ownerId}>" in content) {
            event.message.addReaction(Emoji.COFFEE).queue()
        }

        // Help the user that couldn't read the activity text by sending them the prefix. :P
        if (content == "<@${bot.jda.selfUser.id}>") {
            GlobalScope.launch { event.channel.sendSuccess("My prefix here is `..`!") }
        }
    }
}
