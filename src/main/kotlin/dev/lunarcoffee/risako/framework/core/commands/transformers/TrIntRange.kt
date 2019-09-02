package dev.lunarcoffee.risako.framework.core.commands.transformers

import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.*

class TrIntRange(
    override val optional: Boolean = false,
    override val default: IntRange = 0..0
) : Transformer<IntRange> {

    override suspend fun transform(
        ctx: CommandContext,
        args: MutableList<String>
    ): OpResult<IntRange> {

        val match = INT_RANGE.matchEntire(args.firstOrNull() ?: "")

        return OpSuccess(
            if (optional && match == null) {
                default
            } else {
                val (startStr, endStr) = match?.destructured ?: return returnOrThrow()
                val start = startStr.toIntOrNull() ?: return returnOrThrow()
                val end = endStr.toIntOrNull() ?: return returnOrThrow()

                if (start >= end)
                    return returnOrThrow()
                start..end
            }
        )
    }

    // Returns if the argument is optional, throws an exception otherwise.
    private fun returnOrThrow(): OpResult<IntRange> {
        return if (optional) OpSuccess(default) else OpError()
    }

    companion object {
        private val INT_RANGE = """(\d+)-(\d+)""".toRegex()
    }
}
