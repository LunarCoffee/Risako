@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.commands.utility

import dev.lunarcoffee.risako.bot.exts.commands.utility.fact.FastFactorialCalculator
import dev.lunarcoffee.risako.bot.exts.commands.utility.help.HelpTextGenerator
import dev.lunarcoffee.risako.bot.exts.commands.utility.remind.ReminderReloader
import dev.lunarcoffee.risako.bot.exts.commands.utility.rpn.RPNCalculator
import dev.lunarcoffee.risako.framework.api.dsl.command
import dev.lunarcoffee.risako.framework.api.dsl.messagePaginator
import dev.lunarcoffee.risako.framework.api.extensions.*
import dev.lunarcoffee.risako.framework.core.DB
import dev.lunarcoffee.risako.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.commands.transformers.*
import dev.lunarcoffee.risako.framework.core.services.reloaders.Reloadable
import dev.lunarcoffee.risako.framework.core.std.*
import dev.lunarcoffee.risako.framework.core.trimToDescription
import java.time.Instant
import java.util.*

@CommandGroup("Utility")
internal class UtilityCommands(private val bot: Bot) {
    fun rpn() = command("rpn") {
        description = "Reverse polish notation calculator! I'm not sure why this exists."
        aliases = arrayOf("reversepolish")

        extDescription = """
            |`$name expression`\n
            |Calculates the result of a expression in reverse Polish notation (postfix notation).
            |The supported operators are: [`+`, `-`, `*`, `/`, `**`, `%`, `&`, `|`, `^`]
        """.trimToDescription()

        expectedArgs = arrayOf(TrSplit())
        execute { args ->
            val expression = args.get<List<String>>(0)

            when (val result = RPNCalculator(expression).calculate()) {
                is OpSuccess -> sendSuccess("The result of the calculation is `${result.result}`!")
                is OpError -> sendError("Something was wrong with your expression!")
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
        execute { args ->
            val rawText = args.get<String>(0)
            val byWords = rawText.endsWith(" -w")

            val text = if (byWords) {
                rawText.split(" ").dropLast(1).reversed().joinToString(" ")
            } else {
                rawText.reversed()
            }

            sendSuccess("Your text reversed is `$text`")
        }
    }

    fun fact() = command("fact") {
        description = "Calculates the factorial of a given number."
        aliases = arrayOf("factorial")

        extDescription = """
            |`$name number`\n
            |A lot of online calculators stop giving you factorials in whole numbers after quite an
            |early point, usually around `15!` or so. Unlike them, I'll calculate factorials up to
            |50000 and happily provide them in all their glory.
        """.trimToDescription()

        expectedArgs = arrayOf(TrInt())
        execute { args ->
            val number = args.get<Int>(0).toLong()
            if (number !in 0..50_000) {
                sendError("I can't calculate the factorial of that number!")
                return@execute
            }
            val result = FastFactorialCalculator.factorial(number).toString().chunked(1_777)

            send(
                messagePaginator {
                    for (chunk in result) {
                        page("```$chunk```")
                    }
                }
            )
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
        execute { args ->
            val rawText = args.get<String>(0)
            val byWords = rawText.endsWith(" -w")

            val length = if (byWords) rawText.split(" ").size - 1 else rawText.length
            val charsOrWords = if (byWords) "words" else "characters"

            sendSuccess("Your text is `$length` $charsOrWords long.")
        }
    }

    fun remind() = command("remind") {
        val remindCol = DB.getCollection<Reloadable>("Reminders0")

        description = "Sets a reminder so you don't have to remember things!"
        aliases = arrayOf("remindme")

        extDescription = """
            |`$name time [reason]`\n
            |This command takes a time string that looks something like `3h 40m` or `1m 30s` or
            |`2d 4h 32m 58s`, and optionally, a reason to remind you of. After the amount of time
            |specified in `time`, I should ping you in the channel you send the command in and
            |remind you of what you told me.
        """.trimToDescription()

        expectedArgs = arrayOf(TrTime(), TrRest(true, "(no reason)"))
        execute { args ->
            val time = args.get<SplitTime>(0)
            val reason = args.get<String>(1)

            val dateTime = time.localWithoutWeekday().replace(" at ", "` at `")
            sendSuccess("I'll remind you on `$dateTime`!")

            val reminder = ReminderReloader(
                Date.from(Instant.now().plusMillis(time.totalMs)),
                event.guild.id,
                event.channel.id,
                event.author.asMention,
                reason
            )

            remindCol.insertOne(reminder)
            reminder.schedule(event)
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
        execute { args ->
            val commandName = args.get<String>(0)
            val flags = args.get<String>(1)
            val command = bot.commands.find { commandName in it.names }

            if (commandName.isNotBlank() && command == null) {
                sendError("I can't find that command!")
                return@execute
            }

            send(
                if (command == null) {
                    HelpTextGenerator(this).listCommandsEmbed()
                } else {
                    HelpTextGenerator(this).detailedCommandHelpEmbed(command, flags)
                }
            )
        }
    }
}
