package dev.lunarcoffee.risakobot.framework.api.dsl

import dev.lunarcoffee.risakobot.framework.core.bot.DefaultBot
import dev.lunarcoffee.risakobot.framework.core.commands.Command
import dev.lunarcoffee.risakobot.framework.core.commands.GuildCommand

internal inline fun command(name: String, crossinline init: Command.() -> Unit): Command {
    return GuildCommand(DefaultBot.instance, name).apply(init)
}
