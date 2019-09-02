package dev.lunarcoffee.risako.framework.core.commands.transformers

import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.*

class TrDouble(
    override val optional: Boolean = false,
    override val default: Double = 0.0
) : Transformer<Double> {

    override suspend fun transform(
        ctx: CommandContext,
        args: MutableList<String>
    ): OpResult<Double> {

        // Try and return the first value of [args] as a [Double], then try to return [default] if
        // [optional] is true.
        return args
            .firstOrNull()
            ?.toDoubleOrNull()
            ?.run {
                args.removeAt(0)
                OpSuccess(this)
            } ?: if (optional) OpSuccess(default) else OpError()
    }
}
