package dev.lunarcoffee.risako.framework.core.commands.transformers

import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.*

class TrRemaining(
    override val optional: Boolean = false,
    override val default: List<String> = emptyList()
) : Transformer<List<String>> {

    override suspend fun transform(
        ctx: CommandContext,
        args: MutableList<String>
    ): OpResult<List<String>> {

        val joined = args.joinToString(" ")
        if (!optional && joined.isEmpty())
            return OpError()

        return OpSuccess(args.toList()).also { args.clear() }
    }
}
