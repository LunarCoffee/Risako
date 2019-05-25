package dev.lunarcoffee.risako.framework.core.dispatchers

import dev.lunarcoffee.risako.framework.core.commands.*
import dev.lunarcoffee.risako.framework.core.dispatchers.parsers.*
import dev.lunarcoffee.risako.framework.core.std.*
import net.dv8tion.jda.api.hooks.*

internal interface Dispatcher : EventListener, HasBot {
    val commands: List<Command>
    val argParser: ArgParser

    fun addCommand(command: Command)
    fun registerAllCommands()
}
