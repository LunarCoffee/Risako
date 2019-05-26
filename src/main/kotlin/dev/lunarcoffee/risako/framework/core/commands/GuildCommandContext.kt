package dev.lunarcoffee.risako.framework.core.commands

import dev.lunarcoffee.risako.framework.core.bot.Bot
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

internal class GuildCommandContext(
    override val event: GuildMessageReceivedEvent,
    override val bot: Bot
) : CommandContext, TextChannel by event.channel
