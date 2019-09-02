package dev.lunarcoffee.risako.framework.core.dispatchers

import dev.lunarcoffee.risako.framework.core.commands.Command
import dev.lunarcoffee.risako.framework.core.dispatchers.parsers.ArgParser
import dev.lunarcoffee.risako.framework.core.std.HasBot
import kotlinx.coroutines.CoroutineScope
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener

interface Dispatcher : CoroutineScope, EventListener, HasBot {
    val commands: List<Command>
    val argParser: ArgParser

    fun addCommand(command: Command)
    fun registerAllCommands()

    suspend fun handleEvent(event: GenericEvent)
}
