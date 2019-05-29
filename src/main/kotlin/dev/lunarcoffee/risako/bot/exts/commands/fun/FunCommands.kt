@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.commands.`fun`

import dev.lunarcoffee.risako.bot.exts.commands.`fun`.flip.CoinFlipSender
import dev.lunarcoffee.risako.bot.exts.commands.`fun`.roll.*
import dev.lunarcoffee.risako.framework.api.dsl.command
import dev.lunarcoffee.risako.framework.api.extensions.sendError
import dev.lunarcoffee.risako.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.commands.transformers.TrGreedy
import dev.lunarcoffee.risako.framework.core.commands.transformers.TrInt

@CommandGroup("Fun")
internal class FunCommands(private val bot: Bot) {
    fun flip() = command("flip") {
        description = "Flips coins."
        aliases = arrayOf("coin", "flipcoin")

        extDescription = """
            |`$name [times]`\n
            |If an argument is provided, this command flips `times` coins, displaying all of the
            |flip results. If no argument is provided, this command will flip one coin. I can flip
            |anywhere from 1 to 10000 coins.
        """

        expectedArgs = arrayOf(TrInt(true, 1))
        execute { args ->
            val times = args.get<Int>(0)
            if (times !in 1..10_000) {
                sendError("I can't flip that number of coins!")
                return@execute
            }
            CoinFlipSender(times).send(this)
        }
    }

    fun roll() = command("roll") {
        description = "Rolls dice with RPG style roll specifiers."
        aliases = arrayOf("dice", "rolldice")

        extDescription = """
            |`$name [roll specs...]`\n
            |Rolls dice according to roll specifiers. Some examples are:\n
            | - `d6`: rolls a six-sided die\n
            | - `2d8`: rolls two eight-sided dice\n
            | - `d20+1`: rolls a twenty-sided die and adds one to the result\n
            | - `3d4-2`: rolls three four-sided dice and subtracts two from the result\n
            |If no specifiers are provided, a single `d6` is used.
        """

        expectedArgs = arrayOf(TrGreedy(String::toDiceRoll, true, DiceRoll(1, 6, 0)))
        execute { args ->
            val diceRolls = args.get<List<DiceRoll>>(0)
            if (diceRolls.size > 100) {
                sendError("I can't roll that many specifiers!")
                return@execute
            }

            // Check for constraints with helpful feedback.
            for (roll in diceRolls) {
                val errorMsg = when {
                    roll.times !in 1..100 -> "I can't roll a die that many times!"
                    roll.sides !in 1..1_000 -> "I can't roll a die with that many sides!"
                    roll.mod !in -10_000..10_000 -> "That modifier is too big or small!"
                    else -> ""
                }

                if (errorMsg.isNotEmpty()) {
                    sendError(errorMsg)
                    return@execute
                }
            }
            DiceRollSender(diceRolls).send(this)
        }
    }
}
