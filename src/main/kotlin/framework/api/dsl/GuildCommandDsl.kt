package framework.api.dsl

import framework.core.bot.DefaultBot
import framework.core.commands.Command
import framework.core.commands.GuildCommand

internal inline fun command(name: String, crossinline init: Command.() -> Unit): Command {
    return GuildCommand(DefaultBot.instance, name).apply(init)
}
