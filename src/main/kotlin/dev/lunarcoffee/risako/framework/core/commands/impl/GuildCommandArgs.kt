package dev.lunarcoffee.risako.framework.core.commands.impl

import dev.lunarcoffee.risako.framework.core.dispatchers.*

internal class GuildCommandArgs(override val items: List<Any?>) : DispatchableArgs {
    @Suppress("UNCHECKED_CAST")
    override fun <T> get(index: Int) = items[index] as T?
}
