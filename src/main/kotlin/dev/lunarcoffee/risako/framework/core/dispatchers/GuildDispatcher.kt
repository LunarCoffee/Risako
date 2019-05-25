package dev.lunarcoffee.risako.framework.core.dispatchers

import dev.lunarcoffee.risako.framework.core.bot.*
import dev.lunarcoffee.risako.framework.core.commands.*
import dev.lunarcoffee.risako.framework.core.dispatchers.parsers.*
import net.dv8tion.jda.api.events.*
import net.dv8tion.jda.api.events.message.guild.*

internal class GuildDispatcher(
    override val bot: Bot,
    override val argParser: ArgParser
) : Dispatcher {

    override val commands = mutableListOf<Command>()

    override fun addCommand(command: Command) {
        commands += command
    }

    override fun registerAllCommands() {
        bot.jda.apply {
            removeEventListener(this)
            addEventListener(this)
        }
    }

    override fun onEvent(event: GenericEvent) {
        if (event !is GuildMessageReceivedEvent) {
            return
        }
        println("omega  messag! !")
    }
}
