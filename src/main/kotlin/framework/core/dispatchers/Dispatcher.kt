package framework.core.dispatchers

import framework.core.commands.Command
import framework.core.dispatchers.parsers.ArgParser
import framework.core.std.HasBot
import kotlinx.coroutines.CoroutineScope
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener

internal interface Dispatcher : CoroutineScope, EventListener, HasBot {
    val commands: List<Command>
    val argParser: ArgParser

    fun addCommand(command: Command)
    fun registerAllCommands()

    suspend fun handleEvent(event: GenericEvent)
}
