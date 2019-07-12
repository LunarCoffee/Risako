package bot.exts.commands.misc.loc

import bot.consts.Emoji
import framework.api.dsl.embed
import framework.api.extensions.send
import framework.core.commands.CommandContext
import framework.core.std.ContentSender

internal class CodeStatsSender(private val stats: CodeStats) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.send(
            embed {
                stats.run {
                    title = "${Emoji.OPEN_FILE_FOLDER}  Code stats:"
                    description = """
                        **Lines of code**: $linesOfCode
                        **Lines with content**: ${linesOfCode - blankLines}
                        **Blank lines**: $blankLines
                        **Characters**: $characters
                        **Code files**: $fileCount
                        **Directories**: $dirs
                    """.trimIndent()
                }
            }
        )
    }
}
