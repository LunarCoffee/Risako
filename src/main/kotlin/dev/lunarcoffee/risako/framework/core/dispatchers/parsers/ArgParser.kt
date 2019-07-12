package dev.lunarcoffee.risako.framework.core.dispatchers.parsers

interface ArgParser {
    fun parseArgs(content: String): List<String>
}
