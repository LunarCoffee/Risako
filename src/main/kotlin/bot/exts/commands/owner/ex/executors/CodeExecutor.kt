package bot.exts.commands.owner.ex.executors

import bot.exts.commands.owner.ex.ExecResult
import framework.core.commands.CommandContext

internal interface CodeExecutor {
    suspend fun execute(ctx: CommandContext): ExecResult
}
