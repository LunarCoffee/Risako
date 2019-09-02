package dev.lunarcoffee.risako.framework.core.commands

import dev.lunarcoffee.risako.framework.core.dispatchers.DispatchableContext
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface CommandContext : DispatchableContext, MessageChannel {
    override val event: MessageReceivedEvent
}
