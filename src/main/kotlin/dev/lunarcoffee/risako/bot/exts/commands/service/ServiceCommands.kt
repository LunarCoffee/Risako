@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.commands.service

import dev.lunarcoffee.risako.bot.exts.commands.service.iss.IssStatsSender
import dev.lunarcoffee.risako.bot.exts.commands.service.xkcd.XkcdRequester
import dev.lunarcoffee.risako.bot.exts.commands.service.xkcd.XkcdSender
import dev.lunarcoffee.risako.framework.api.dsl.command
import dev.lunarcoffee.risako.framework.api.extensions.sendError
import dev.lunarcoffee.risako.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.commands.transformers.TrWord
import kotlin.random.Random

@CommandGroup("Service")
internal class ServiceCommands(private val bot: Bot) {
    fun xkcd() = command("xkcd") {
        description = "Gets an xkcd comic!"
        aliases = arrayOf("getxkcd")

        extDescription = """
            |`$name [number|-r]`\n
            |Gets and displays information about the xkcd comic number `number`. If `number` is not
            |specified, the latest comic is used. If the `-r` flag is set, a random comic will be
            |used. Of course, this command also displays the comic itself, not just information.
        """

        expectedArgs = arrayOf(TrWord(true))
        execute { args ->
            val whichOrRandom = args.get<String>(0)
            val latestNumber = XkcdRequester(null).get().num.toInt()

            val which = when (whichOrRandom) {
                "" -> latestNumber
                "-r" -> Random.nextInt(latestNumber) + 1
                else -> whichOrRandom.toIntOrNull()
            }

            if (which == null || which !in 1..latestNumber) {
                sendError("I can't get the comic with that number!")
                return@execute
            }
            XkcdSender(which).send(this)
        }
    }

    fun iss() = command("iss") {
        description = "Shows the current location of the ISS."
        aliases = arrayOf("issinfo", "spacestation")

        extDescription = """
            |`$name`\n
            |Shows details about the location and other info of the International Space Station. A
            |map with a point where the ISS currently is will also be displayed. The information is
            |fetched using the `Where the ISS at?` API.
        """

        execute { IssStatsSender.send(this) }
    }
}
