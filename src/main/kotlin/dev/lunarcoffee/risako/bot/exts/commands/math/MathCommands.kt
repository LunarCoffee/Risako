@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.commands.math

import dev.lunarcoffee.risako.bot.exts.commands.math.fact.FastFactorialCalculator
import dev.lunarcoffee.risako.bot.exts.commands.math.rpn.RPNCalculationSender
import dev.lunarcoffee.risako.framework.api.dsl.command
import dev.lunarcoffee.risako.framework.api.dsl.messagePaginator
import dev.lunarcoffee.risako.framework.api.extensions.*
import dev.lunarcoffee.risako.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.commands.transformers.*
import java.security.SecureRandom
import kotlin.random.Random

@CommandGroup("Math")
internal class MathCommands(private val bot: Bot) {
    fun rng() = command("rng") {
        val secureRandom = SecureRandom()

        description = "Gets you a random number between two numbers (inclusive)."
        aliases = arrayOf("rand", "random")

        extDescription = """
            |`$name low high [-s]`\n
            |This command generates a random number within the closed interval [`low`, `high`]. If
            |the `-s` flag is set, a more secure source of randomness will be used.
        """

        expectedArgs = arrayOf(TrInt(), TrInt(), TrWord(true))
        execute { args ->
            val lowerBound = args.get<Int>(0)
            val upperBound = args.get<Int>(1) + 1
            val secure = args.get<String>(2) == "-s"

            val number = if (secure) {
                secureRandom.nextInt(upperBound - lowerBound) + lowerBound
            } else {
                Random.nextInt(lowerBound, upperBound)
            }

            val secureText = if (secure) " secure" else ""
            sendSuccess("Your$secureText random number is `$number`!")
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
        """

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

    fun rpn() = command("rpn") {
        description = "Reverse polish notation calculator! I'm not sure why this exists."
        aliases = arrayOf("reversepolish")

        extDescription = """
            |`$name expression`\n
            |Calculates the result of a expression in reverse Polish notation (postfix notation).
            |The supported operators are: [`+`, `-`, `*`, `/`, `**`, `%`, `&`, `|`, `^`]
        """

        expectedArgs = arrayOf(TrSplit())
        execute { args ->
            val expression = args.get<List<String>>(0)
            send(RPNCalculationSender(expression))
        }
    }

    fun plot() = command("plot") {
        description = "Plots one or more given functions on a plane."
        aliases = arrayOf("graph", "plotfunc", "graphfunc")

        extDescription = """
            |`$name functions...`
        """

        expectedArgs = arrayOf(TrSplit())
        execute { args ->
            sendError("This command is not yet implemented!")
        }
    }
}
