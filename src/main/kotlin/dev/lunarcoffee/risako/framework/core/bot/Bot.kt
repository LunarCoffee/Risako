package dev.lunarcoffee.risako.framework.core.bot

import dev.lunarcoffee.risako.framework.core.commands.*
import dev.lunarcoffee.risako.framework.core.dispatchers.*
import net.dv8tion.jda.api.*
import net.dv8tion.jda.api.hooks.*

internal interface Bot {
    val jda: JDA
    val config: BotConfig
    val dispatcher: Dispatcher

    val commands: List<Command>
    val commandNames: List<String>

    val listeners: List<EventListener>
    val listenerNames: List<String>

    fun addCommand(command: Command)
    fun addListener(listener: EventListener)

    fun loadAllCommands()
}
