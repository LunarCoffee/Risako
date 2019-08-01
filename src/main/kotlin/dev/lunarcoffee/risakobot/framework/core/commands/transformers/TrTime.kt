package dev.lunarcoffee.risakobot.framework.core.commands.transformers

import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import dev.lunarcoffee.risakobot.framework.core.std.*

internal class TrTime(
    override val optional: Boolean = false,
    override val default: SplitTime = SplitTime.NONE
) : Transformer<SplitTime> {

    override suspend fun transform(
        ctx: CommandContext,
        args: MutableList<String>
    ): OpResult<SplitTime> {

        val isTime = args.takeWhile { it.matches(TIME_REGEX) }
        if (isTime.isEmpty()) {
            return if (optional) {
                OpSuccess(default)
            } else {
                OpError()
            }
        }
        args.removeAll(isTime)

        val units = isTime.map { timePart ->
            TIME_REGEX.matchEntire(timePart)!!.groupValues[0].partition { it in "dhms" }.run {
                Pair(first, second.toLong())
            }
        }.toMap()

        return OpSuccess(
            units.run {
                SplitTime(
                    getOrDefault("d", 0),
                    getOrDefault("h", 0),
                    getOrDefault("m", 0),
                    getOrDefault("s", 0)
                )
            }
        )
    }

    companion object {
        private val TIME_REGEX = """((\d*d)|(\d*h)|(\d*m)|(\d*s))""".toRegex()
    }
}
