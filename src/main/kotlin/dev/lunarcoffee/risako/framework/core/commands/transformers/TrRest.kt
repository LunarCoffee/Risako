package dev.lunarcoffee.risako.framework.core.commands.transformers

import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.*

internal class TrRest(
    override val optional: Boolean = false,
    override val default: String = ""
) : Transformer<String> {

    override suspend fun transform(
        ctx: CommandContext,
        args: MutableList<String>
    ): OpResult<String> {

        val joined = args.joinToString(" ")
        if (!optional && joined.isEmpty()) {
            return OpError()
        }

        return OpSuccess(
            if (optional && args.isEmpty()) {
                default
            } else {
                args.clear()
                joined
            }
        )
    }
}
