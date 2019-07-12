@file:Suppress("unused")

package bot.exts.commands.service

import bot.exts.commands.service.iss.IssStatsSender
import bot.exts.commands.service.osu.beatmap.OsuBeatmapSender
import bot.exts.commands.service.osu.user.OsuUserSender
import bot.exts.commands.service.xkcd.XkcdRequester
import bot.exts.commands.service.xkcd.XkcdSender
import framework.api.dsl.command
import framework.api.extensions.send
import framework.api.extensions.sendError
import framework.core.annotations.CommandGroup
import framework.core.bot.Bot
import framework.core.commands.transformers.TrWord
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
            send(XkcdSender(which))
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

        execute { send(IssStatsSender) }
    }

    fun osu() = command("osu") {
        description = "Does lots of osu! related stuff!"
        aliases = arrayOf("ous")

        extDescription = """
            |`$name action username|userid|beatmapid [mode]`\n
            |This command does osu! related stuff depending on the provided `action`.\n
            |&{Getting user info:}
            |If the action is `user`, I will get info for for the user with the provided `username`
            |or `userid`. This info includes things like their rank, accuracy, PP, and more.
            |&{Getting beatmap info:}
            |If the action is `beatmap`, I will get info of the beatmap with the provided id of
            |`beatmapid`. This info includes the creator, star rating, BPM, AR, drain, and more.
            |&{Selecting a gamemode:}
            |With the `mode` argument, you can specify what mode to get info about. It should be
            |`normal`, `taiko`, `catch`, or `mania`. If the action is `user`, I will get the user's
            |stats in your selected gamemode, and if the action is `beatmap`, I will get the
            |beatmaps of that gamemode for the set you specified.
        """

        expectedArgs = arrayOf(TrWord(), TrWord(), TrWord(true))
        execute { args ->
            val action = args.get<String>(0)
            val userOrBeatmap = args.get<String>(1)
            val mode = when (args.get<String>(2).toLowerCase()) {
                "", "normal" -> 0
                "taiko" -> 1
                "catch" -> 2
                "mania" -> 3
                else -> {
                    sendError("That isn't a valid gamemode!")
                    return@execute
                }
            }

            send(
                when (action) {
                    "user" -> OsuUserSender(userOrBeatmap, mode)
                    "beatmap" -> OsuBeatmapSender(userOrBeatmap, mode)
                    else -> {
                        sendError("That's an invalid operation!")
                        return@execute
                    }
                }
            )
        }
    }
}
