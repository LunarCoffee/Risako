package dev.lunarcoffee.risakobot.framework.core.commands

import dev.lunarcoffee.risakobot.framework.core.dispatchers.DispatchableContext
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

internal interface CommandContext : DispatchableContext, MessageChannel {
    override val event: MessageReceivedEvent
}
