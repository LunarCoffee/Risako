package framework.core.commands.transformers

import framework.core.commands.CommandContext
import framework.core.std.OpResult

internal interface Transformer<T> {
    val optional: Boolean
    val default: T

    suspend fun transform(ctx: CommandContext, args: MutableList<String>): OpResult<T>
}
