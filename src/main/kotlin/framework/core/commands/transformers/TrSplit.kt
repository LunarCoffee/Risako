package framework.core.commands.transformers

import framework.core.commands.CommandContext
import framework.core.std.*

internal class TrSplit(
    private val separator: String = " ",
    override val optional: Boolean = false,
    override val default: List<String> = emptyList()
) : Transformer<List<String>> {

    override suspend fun transform(
        ctx: CommandContext,
        args: MutableList<String>
    ): OpResult<List<String>> {

        return if (args.isEmpty()) {
            OpError()
        } else {
            OpSuccess(args.joinToString(" ").split(separator).also { args.clear() })
        }
    }
}
