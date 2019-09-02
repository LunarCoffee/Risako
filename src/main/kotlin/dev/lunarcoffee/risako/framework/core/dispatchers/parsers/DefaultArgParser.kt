package dev.lunarcoffee.risako.framework.core.dispatchers.parsers

class DefaultArgParser : ArgParser {
    override fun parseArgs(content: String): List<String> {
        val args = mutableListOf<String>()
        var pos = 0

        while (pos < content.length) {
            when {
                content[pos] == ' ' -> pos++
                content[pos] == '"' -> {
                    args += content.drop(pos + 1).takeWhile { it != '"' }
                    pos += args.last().length + 2
                }
                else -> {
                    args += content.drop(pos).takeWhile { it != ' ' }
                    pos += args.last().length
                }
            }
        }

        // Drop the first to remove the command name.
        return args.drop(1)
    }
}
