package dev.lunarcoffee.risako.framework.core.dispatchers

import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.commands.Command
import dev.lunarcoffee.risako.framework.core.dispatchers.parsers.ArgParser
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

internal class GuildDispatcher(
    override val bot: Bot,
    override val argParser: ArgParser
) : Dispatcher {

    override val commands = mutableListOf<Command>()

    override fun addCommand(command: Command) {
        commands += command
    }

    override fun registerAllCommands() {
        bot.jda.removeEventListener(this)
        bot.jda.addEventListener(this)
    }

    override fun onEvent(event: GenericEvent) {
        if (event !is GuildMessageReceivedEvent) {
            return
        }
        println("omega  messag! !")
    }
}
