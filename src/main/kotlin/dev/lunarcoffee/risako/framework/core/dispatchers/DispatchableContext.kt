package dev.lunarcoffee.risako.framework.core.dispatchers

import dev.lunarcoffee.risako.framework.core.std.HasBot
import net.dv8tion.jda.api.events.GenericEvent

internal interface DispatchableContext : HasBot {
    val event: GenericEvent
}
