package dev.lunarcoffee.risakobot.framework.core.commands.transformers

import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import dev.lunarcoffee.risakobot.framework.core.std.OpResult

internal interface Transformer<T> {
    val optional: Boolean
    val default: T

    suspend fun transform(ctx: CommandContext, args: MutableList<String>): OpResult<T>
}
