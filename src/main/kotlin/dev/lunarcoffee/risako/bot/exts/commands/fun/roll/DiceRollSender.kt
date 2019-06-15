package dev.lunarcoffee.risako.bot.exts.commands.`fun`.roll

import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.dsl.embedPaginator
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.ContentSender
import kotlin.random.Random

internal class DiceRollSender(private val diceRolls: List<DiceRoll>) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        // Generate a list of lists that hold each result for each roll.
        val results = diceRolls.map { roll -> List(roll.times) { Random.nextInt(roll.sides) + 1 } }

        // Sum all the results of each individual roll and add all the modifiers.
        val total = results.flatten().sum() + diceRolls.sumBy { it.mod }

        // If the user rolls more than one die, make the embed title "You rolled a total of..."
        // instead of "You rolled a..." if only one die was rolled. Makes it a bit more human.
        val totalOfOrEmpty = if (diceRolls.size > 1) "total of " else ""

        // Make each roll specifier's results look like "**2d8-2**: [3, 6] -2" or so.
        val resultPages = results.zip(diceRolls).map { (res, roll) ->
            val modifierSign = if (roll.mod <= 0) "" else "+"
            val modifier = if (roll.mod != 0) roll.mod.toString() else ""

            val modifierAndSign = modifierSign + modifier
            "**${roll.times}d${roll.sides}$modifierAndSign**: $res $modifierAndSign"
        }.chunked(16).map { it.joinToString("\n") }

        ctx.send(
            ctx.embedPaginator {
                for (page in resultPages) {
                    page(
                        embed {
                            title = "${Emoji.GAME_DIE}  You rolled a $totalOfOrEmpty$total!"
                            description = page
                        }
                    )
                }
            }
        )
    }
}
