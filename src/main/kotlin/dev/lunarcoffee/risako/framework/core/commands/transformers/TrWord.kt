package dev.lunarcoffee.risako.framework.core.commands.transformers

import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.*

internal class TrWord(
    override val optional: Boolean = false,
    override val default: String = ""
) : Transformer<String> {

    override suspend fun transform(
        ctx: CommandContext,
        args: MutableList<String>
    ): OpResult<String> {

        return args
            .firstOrNull()
            ?.run {
                args.removeAt(0)
                OpSuccess(this)
            } ?: if (optional) OpSuccess(default) else OpError()
    }
}
