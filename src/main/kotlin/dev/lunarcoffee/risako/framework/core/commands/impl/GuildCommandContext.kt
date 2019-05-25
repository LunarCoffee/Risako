package dev.lunarcoffee.risako.framework.core.commands.impl

import dev.lunarcoffee.risako.framework.core.bot.*
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.guild.*

internal class GuildCommandContext(
    override val event: GuildMessageReceivedEvent,
    override val bot: Bot
) : CommandContext, MessageChannel by event.channel
