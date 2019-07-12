package framework.core.commands.transformers

import framework.core.commands.CommandContext
import framework.core.std.*

internal class TrGreedy<T>(
    val conversionFunction: (String) -> T,
    override val optional: Boolean = false,
    vararg defaults: T
) : Transformer<List<T>> {

    override val default = defaults.toList()

    override suspend fun transform(
        ctx: CommandContext,
        args: MutableList<String>
    ): OpResult<List<T>> {

        if (args.isEmpty()) {
            return if (optional) OpSuccess(default) else OpError()
        }

        val result = mutableListOf<T>()
        var numTaken = 0

        for (arg in args) {
            try {
                val item = args[0 + numTaken++]
                result.add(conversionFunction(item))
            } catch (e: Exception) {
                break
            }
        }
        args.removeAll(args.take(numTaken))

        return if (result.isEmpty()) {
            if (optional) OpSuccess(default) else OpError()
        } else {
            OpSuccess(result)
        }
    }
}
