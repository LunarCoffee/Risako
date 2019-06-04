@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.commands.`fun`

import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.bot.exts.commands.`fun`.eightball.EightBallSender
import dev.lunarcoffee.risako.bot.exts.commands.`fun`.flip.CoinFlipSender
import dev.lunarcoffee.risako.bot.exts.commands.`fun`.msp.MinesweeperGenerator
import dev.lunarcoffee.risako.bot.exts.commands.`fun`.roll.*
import dev.lunarcoffee.risako.bot.exts.commands.`fun`.rplace.RPlaceCanvas
import dev.lunarcoffee.risako.framework.api.dsl.command
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.api.extensions.sendError
import dev.lunarcoffee.risako.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.commands.transformers.*

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
            send(CoinFlipSender(times))
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
            send(DiceRollSender(diceRolls))
        }
    }

    fun pick() = command("pick") {
        description = "Picks a value from the options you give."
        aliases = arrayOf("select", "choose")

        extDescription = """
            |`$name options...`\n
            |Picks a value from `options`, which is a list of choices. To have an option name with
            |a space, wrap the name in quotes "like this." This also applies to other commands.
        """

        expectedArgs = arrayOf(TrSplit())
        execute { args ->
            val options = args.get<List<String>>(0)
            if (options.size < 2) {
                sendError("I need at least 2 options to choose from!")
                return@execute
            }

            send(
                embed {
                    title = "${Emoji.THINKING}  I choose **${options.random()}**!"
                    description = options.toString()
                }
            )
        }
    }

    fun eightBall() = command("8ball") {
        description = "Uncover secrets with the 100% reliable Magic 8 Ball!"
        aliases = arrayOf("magic8ball", "magiceightball")

        extDescription = """
            |`$name question`\n
            |Ask the Magic 8 Ball a question and it will undoubtedly tell you the truth (unless
            |it's tired and wants to sleep and not answer your question, in which case you should
            |simply ask again, politely).
        """

        expectedArgs = arrayOf(TrRest())
        execute { send(EightBallSender()) }
    }

    fun msp() = command("msp") {
        description = "Generates a minesweeper game."
        aliases = arrayOf("minesweeper")

        extDescription = """
            |`$name [size] [mines]`\n
            |Gives you a minesweeper game made with spoiler tags. By default, the board is 8x8 and
            |has 10 mines, but you can change the number of mines by specifying `mines`, a number
            |between 6 and 12 (inclusive).
        """

        expectedArgs = arrayOf(TrInt(true, 8), TrInt(true, 10))
        execute { args ->
            val size = args.get<Int>(0)
            val mines = args.get<Int>(1)

            if (size !in 6..12) {
                sendError("I can't make the grid that size!")
                return@execute
            }

            if (mines !in 1 until size * size) {
                sendError("I can't add that many mines!")
                return@execute
            }
            send(MinesweeperGenerator(size, mines).generateEmbed())
        }
    }

    fun rplace() = command("rplace") {
        description = "An open drawing canvas similar to r/place."
        aliases = arrayOf("redditplace")

        extDescription = """
            |`$name [nogrid|raw|colors|put|snap|dsnap|gallery] [x] [y] [color|snapshotname]`\n
            |A small r/place in Discord! The first argument should be an action to perform.
            |&{Viewing the canvas:}
            |If the action is empty, I will send you a picture of the canvas as of now.\n
            |If it is `nogrid`, I'll send you an image of the canvas without the grid, as well the
            |stats embed.\n
            |If it is `raw`, I'll send you only the image of the canvas.
            |&{Drawing on the canvas:}
            |It the action is `colors`, I'll send you all the available colors you can use.\n
            |If it is `put`, you should specify three more arguments: the `x` coordinate, `y`
            |coordinate, and `color` you want your pixel to be. Like on a cartesian plane, the x
            |axis goes horizontally and the y axis goes vertically.
            |&{Taking and viewing snapshots:}
            |If the action is `snap`, I will save the current canvas as an image with the name
            |given by `snapshotname`, which can be accessed using the `gallery` action.\n
            |If it is `dsnap`, I will delete the snapshot with the name `snapshotname`. For safety
            |reasons, only my owner can delete snapshots.\n
            |Finally, if the action is `gallery`, I will list all previously taken snapshots of the
            |canvas, with their names and the times at which they were taken.
            |&{Notes:}
            |You can only place a pixel every 5 minutes, and that the canvas is shared
            |across all the servers I'm in (meaning any changes anyone else makes is reflected
            |everywhere).
        """

        expectedArgs = arrayOf(TrWord(true), TrInt(true), TrInt(true), TrWord(true))
        execute { args ->
            RPlaceCanvas.apply {
                when (args.get<String>(0)) {
                    "" -> sendCanvas(this@execute)
                    "nogrid" -> sendCanvas(this@execute, false)
                    "raw" -> sendCanvas(this@execute, null)
                    "colors" -> sendColors(this@execute)
                    "put" -> drawPixel(this@execute, args)
                    "snap" -> takeSnapshot(this@execute, args)
                    "dsnap" -> deleteSnapshot(this@execute, args)
                    "gallery" -> sendGallery(this@execute, args)
                    else -> sendError("That's not a valid operation!")
                }
            }
        }
    }
}
