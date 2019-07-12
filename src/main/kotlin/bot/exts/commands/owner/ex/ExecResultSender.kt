package bot.exts.commands.owner.ex

import framework.api.dsl.messagePaginator
import framework.api.extensions.send
import framework.core.commands.CommandContext
import framework.core.std.ContentSender

internal class ExecResultSender(private val result: ExecResult) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        if (result == ExecResult.ERROR) {
            // No error message is required because the code execution function has already
            // taken care of that.
            return
        }

        ctx.send(
            ctx.messagePaginator {
                result.run {
                    """
                    |--- $header ---
                    |- stderr:$stderr
                    |- stdout:$stdout
                    |+ Returned `$result` in ~${time}ms."""
                        .trimMargin()
                        .replace(ctx.bot.config.token, "[REDACTED]")
                        .lines()
                        .chunked(16)
                        .forEach { page("```diff\n${it.joinToString("\n")}```") }
                }
            }
        )
    }
}
