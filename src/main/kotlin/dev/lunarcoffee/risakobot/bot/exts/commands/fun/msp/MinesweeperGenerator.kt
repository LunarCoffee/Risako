package dev.lunarcoffee.risakobot.bot.exts.commands.`fun`.msp

import dev.lunarcoffee.risakobot.bot.consts.Emoji
import dev.lunarcoffee.risakobot.framework.api.dsl.embed
import net.dv8tion.jda.api.entities.MessageEmbed
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

internal class MinesweeperGenerator(private val size: Int, private val mines: Int) {
    private val board = Array(size) { IntArray(size) }

    fun generateEmbed(): MessageEmbed {
        generateBoard()
        return embed {
            title = "${Emoji.EXPLODE}  Minesweeper!"
            description = board.joinToString("\n") { row ->
                row.joinToString(" ") { "||:${intToWord(it)}:||" }
            }

            footer { text = "There are $mines mines in total." }
        }
    }

    private fun generateBoard() {
        repeat(mines) { placeMine(Random.nextInt(size), Random.nextInt(size)) }
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (board[j][i] != -1) {
                    board[j][i] = countNeighbouringMines(i, j)
                }
            }
        }
    }

    // Tries to place a mine at a location, picking a new one when a mine is already present.
    private fun placeMine(x: Int, y: Int) {
        if (board[y][x] == -1) {
            placeMine(Random.nextInt(size), Random.nextInt(size))
        } else {
            board[y][x] = -1
        }
    }

    private fun countNeighbouringMines(x: Int, y: Int): Int {
        var mines = 0
        for (i in max(0, x - 1)..min(size - 1, x + 1)) {
            for (j in max(0, y - 1)..min(size - 1, y + 1)) {
                if (board[j][i] == -1) {
                    mines++
                }
            }
        }
        return mines
    }

    private fun intToWord(n: Int): String {
        return when (n) {
            -1 -> "bomb"
            0 -> "zero"
            1 -> "one"
            2 -> "two"
            3 -> "three"
            4 -> "four"
            5 -> "five"
            6 -> "six"
            7 -> "seven"
            8 -> "eight"
            9 -> "nine"
            else -> throw IllegalStateException()
        }
    }
}
