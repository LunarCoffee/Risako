package dev.lunarcoffee.risako.framework.core.commands

import dev.lunarcoffee.risako.framework.core.bot.Bot
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class GuildCommandContext(
    override val event: MessageReceivedEvent,
    override val bot: Bot
) : CommandContext, TextChannel by event.textChannel
