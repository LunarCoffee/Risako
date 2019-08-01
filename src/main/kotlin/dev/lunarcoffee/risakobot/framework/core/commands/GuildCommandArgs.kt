package dev.lunarcoffee.risakobot.framework.core.commands

import dev.lunarcoffee.risakobot.framework.core.dispatchers.DispatchableArgs

internal class GuildCommandArgs(override val items: List<Any?>) : DispatchableArgs {
    @Suppress("UNCHECKED_CAST")
    override fun <T> get(index: Int) = items[index] as T
}
