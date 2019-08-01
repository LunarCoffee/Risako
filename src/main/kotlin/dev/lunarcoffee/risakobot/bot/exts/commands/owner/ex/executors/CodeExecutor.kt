package dev.lunarcoffee.risakobot.bot.exts.commands.owner.ex.executors

import dev.lunarcoffee.risakobot.bot.exts.commands.owner.ex.ExecResult
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext

internal interface CodeExecutor {
    suspend fun execute(ctx: CommandContext): ExecResult
}
