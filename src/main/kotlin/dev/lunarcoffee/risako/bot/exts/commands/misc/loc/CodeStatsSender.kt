package dev.lunarcoffee.risako.bot.exts.commands.misc.loc

import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.ContentSender

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
