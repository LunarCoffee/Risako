package dev.lunarcoffee.risako.framework.core.commands

import dev.lunarcoffee.risako.framework.core.dispatchers.DispatchableArgs

internal class GuildCommandArgs(override val items: List<Any?>) : DispatchableArgs {
    @Suppress("UNCHECKED_CAST")
    override fun <T> get(index: Int) = items[index] as T?
}
