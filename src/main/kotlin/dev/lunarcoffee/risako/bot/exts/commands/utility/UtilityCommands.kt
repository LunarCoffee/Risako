@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.commands.utility

import dev.lunarcoffee.risako.bot.exts.commands.utility.help.HelpTextGenerator
import dev.lunarcoffee.risako.bot.exts.commands.utility.rpn.RPNCalculator
import dev.lunarcoffee.risako.framework.api.dsl.command
import dev.lunarcoffee.risako.framework.api.extensions.*
import dev.lunarcoffee.risako.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.commands.transformers.*
import dev.lunarcoffee.risako.framework.core.std.*
import dev.lunarcoffee.risako.framework.core.trimToDescription

@CommandGroup("Utility")
internal class UtilityCommands(override val bot: Bot) : HasBot {
    fun rpn() = command("rpn") {
        description = "Reverse p4olish notation calculator! I'm not sure why this exists."
        aliases = arrayOf("reversepolish")

        extDescription = """
            |`$name expression`\n
            |Calculates the result of a expression in reverse Polish notation (postfix notation).
            |The supported operators are: [`+`, `-`, `*`, `/`, `**`, `%`, `&`, `|`, `^`]
        """.trimToDescription()

        expectedArgs = arrayOf(TrSplit())
        execute { ctx, args ->
            val expression = args.get<List<String>>(0)

            when (val result = RPNCalculator(expression).calculate()) {
                is OpSuccess -> ctx.success("The result of the calculation is `${result.result}`!")
                is OpError -> ctx.error("Something was wrong with your expression!")
            }
        }
    }

    fun rev() = command("rev") {
        description = "Reverses the given text."
        aliases = arrayOf("reverse", "backwards")

        extDescription = """
            |`$name text [-w]`\n
            |Reverses the given text, letter by letter if the `-w` flag is not specified, and word
            |by word if it is specified (the text is simply split by spaces).
        """.trimToDescription()

        expectedArgs = arrayOf(TrRest())
        execute { ctx, args ->
            val rawText = args.get<String>(0)

            val byWords = rawText.endsWith(" -w")
            val text = if (byWords) {
                rawText.split(" ").dropLast(1).reversed().joinToString(" ")
            } else {
                rawText.reversed()
            }

            ctx.success("Your text reversed is `$text`")
        }
    }

    fun len() = command("len") {
        description = "Shows the length of the given text."
        aliases = arrayOf("length")

        extDescription = """
            |`$name text [-w]`\n
            |Counts the characters in the given text if the `-w` flag is not specified, and counts
            |words if it is specified (the text is simply split by spaces).
        """.trimToDescription()

        expectedArgs = arrayOf(TrRest())
        execute { ctx, args ->
            val rawText = args.get<String>(0)
            val byWords = rawText.endsWith(" -w")

            val length = if (byWords) rawText.split(" ").size - 1 else rawText.length
            val charsOrWords = if (byWords) "words" else "characters"

            ctx.success("Your text is `$length` $charsOrWords long.")
        }
    }

    fun help() = command("help") {
        description = "Lists all commands or shows help for a specific command."
        extDescription = """
            |`$name [command name] [-v]`\n
            |With a command name, this command gets its aliases, expected usage, expected
            |arguments, and optionally (if the `-v` flag is set) an extended description (which
            |you're reading right now). Otherwise, this command simply lists available commands.
            |&{Examples:}
            |Here are some examples of using this command:\n
            | - `..help`: lists all commands\n
            | - `..help osu`: shows general information about the `osu` command\n
            | - `..help rplace -v`: shows very detailed information about the `rplace` command\n
            |Basically, add `-v` (things prefixed with a `-` are called flags) to the end for more
            |detailed help text.
            |&{Reading command usages:}
            |The syntax of the expected command usage is as follows:\n
            | - `name`: denotes that `name` is required, which may be literal or variable\n
            | - `name1|name2`: denotes that either `name1` or `name2` is valid\n
            | - `name...`: denotes that many of `name` can be specified\n
            |If an argument is wrapped with square brackets, it is optional. You may wrap an
            |argument with double quotes "like this" to treat it as one instead of multiple.
        """

        expectedArgs = arrayOf(TrWord(true), TrWord(true))
        execute { ctx, args ->
            val commandName = args.get<String>(0)
            val flags = args.get<String>(1)
            val command = ctx.bot.commands.find { commandName in it.names }

            if (commandName.isNotBlank() && command == null) {
                ctx.error("I can't find that command!")
                return@execute
            }

            ctx.send(
                if (command == null) {
                    HelpTextGenerator(ctx).listCommandsEmbed()
                } else {
                    HelpTextGenerator(ctx).detailedCommandHelpEmbed(command, flags)
                }
            )
        }
    }
}
