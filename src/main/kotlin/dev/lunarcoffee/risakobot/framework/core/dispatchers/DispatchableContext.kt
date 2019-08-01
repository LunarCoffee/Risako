package dev.lunarcoffee.risakobot.framework.core.dispatchers

import dev.lunarcoffee.risakobot.framework.core.std.HasBot
import net.dv8tion.jda.api.events.GenericEvent

internal interface DispatchableContext : HasBot {
    val event: GenericEvent
}
