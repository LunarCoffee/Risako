package dev.lunarcoffee.risakobot.framework.core.dispatchers.parsers

interface ArgParser {
    fun parseArgs(content: String): List<String>
}
