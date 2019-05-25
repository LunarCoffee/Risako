package dev.lunarcoffee.risako.framework.core.commands

import dev.lunarcoffee.risako.framework.core.bot.Bot
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

internal class GuildCommandContext(
    override val event: GuildMessageReceivedEvent,
    override val bot: Bot
) : CommandContext, MessageChannel by event.channel
