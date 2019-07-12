package framework.core.commands

import framework.core.dispatchers.DispatchableContext
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

internal interface CommandContext : DispatchableContext, MessageChannel {
    override val event: MessageReceivedEvent
}
