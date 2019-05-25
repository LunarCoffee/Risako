package dev.lunarcoffee.risako.framework.core.dispatchers

import dev.lunarcoffee.risako.framework.core.commands.Command
import dev.lunarcoffee.risako.framework.core.dispatchers.parsers.ArgParser
import dev.lunarcoffee.risako.framework.core.std.HasBot
import net.dv8tion.jda.api.hooks.EventListener

internal interface Dispatcher : EventListener, HasBot {
    val commands: List<Command>
    val argParser: ArgParser

    fun addCommand(command: Command)
    fun registerAllCommands()
}
