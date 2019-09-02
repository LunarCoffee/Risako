package dev.lunarcoffee.risako.bot.exts.commands.owner.ex.executors

import dev.lunarcoffee.risako.bot.exts.commands.owner.ex.ExecResult
import dev.lunarcoffee.risako.framework.core.commands.CommandContext

interface CodeExecutor {
    suspend fun execute(ctx: CommandContext): ExecResult
}
