package dev.lunarcoffee.risako.bot.exts.commands.misc.loc

import dev.lunarcoffee.risako.framework.core.bot.Bot
import java.io.File

internal class CodeStats(val bot: Bot) {
    var fileCount = 0
    var dirs = 0
    var linesOfCode = 0
    var blankLines = 0
    var characters = 0

    init {
        for (file in File(bot.config.sourceRootDir).walk()) {
            if (file.isDirectory) {
                dirs++
            } else if (file.extension == "kt") {
                fileCount++
                for (line in file.readLines()) {
                    if (line.isBlank()) {
                        blankLines++
                    }
                    linesOfCode++
                    characters += line.length
                }
            }
        }
    }
}
