package dev.lunarcoffee.risako.framework.core.commands.transformers

import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.OpResult

interface Transformer<T> {
    val optional: Boolean
    val default: T

    suspend fun transform(ctx: CommandContext, args: MutableList<String>): OpResult<T>
}
