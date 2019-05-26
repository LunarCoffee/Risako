package dev.lunarcoffee.risako.framework.api.dsl

import dev.lunarcoffee.risako.framework.core.bot.DefaultBot
import dev.lunarcoffee.risako.framework.core.commands.Command
import dev.lunarcoffee.risako.framework.core.commands.GuildCommand

internal inline fun command(name: String, crossinline init: Command.() -> Unit): Command {
    return GuildCommand(DefaultBot.instance, name).apply(init)
}
