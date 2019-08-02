package dev.lunarcoffee.risako.bot.exts.commands.owner.ex.executors

import dev.lunarcoffee.risako.bot.exts.commands.owner.ex.ExecResult
import dev.lunarcoffee.risako.framework.core.commands.CommandContext

internal interface CodeExecutor {
    suspend fun execute(ctx: CommandContext): ExecResult
}
