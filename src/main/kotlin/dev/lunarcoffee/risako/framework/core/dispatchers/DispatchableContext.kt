package dev.lunarcoffee.risako.framework.core.dispatchers

import dev.lunarcoffee.risako.framework.core.std.*
import net.dv8tion.jda.api.events.*

internal interface DispatchableContext : HasBot {
    val event: GenericEvent
}
