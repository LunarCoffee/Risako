@file:Suppress("unused")

package dev.lunarcoffee.risakobot.bot.exts.commands.owner

import dev.lunarcoffee.risakobot.bot.consts.EMBED_COLOR
import dev.lunarcoffee.risakobot.bot.consts.Emoji
import dev.lunarcoffee.risakobot.bot.exts.commands.owner.ex.ExecResultSender
import dev.lunarcoffee.risakobot.bot.exts.commands.owner.ex.executors.KotlinExecutor
import dev.lunarcoffee.risakobot.bot.exts.commands.owner.ex.executors.ShellScriptExecutor
import dev.lunarcoffee.risakobot.bot.exts.commands.owner.file.FileContentSender
import dev.lunarcoffee.risakobot.framework.api.dsl.command
import dev.lunarcoffee.risakobot.framework.api.dsl.embed
import dev.lunarcoffee.risakobot.framework.api.extensions.*
import dev.lunarcoffee.risakobot.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risakobot.framework.core.bot.Bot
import dev.lunarcoffee.risakobot.framework.core.commands.transformers.*
import kotlinx.coroutines.delay
import java.util.regex.PatternSyntaxException
import kotlin.system.exitProcess

@CommandGroup("Owner")
internal class OwnerCommands(private val bot: Bot) {
    fun smsg() = command("smsg") {
        description = "Sends a message. Only my owner can use this."
        aliases = arrayOf("sendmsg")

        ownerOnly = true
        deleteSender = true

        extDescription = """
            |`$name message`\n
            |Sends a message to the command user's channel. This is an owner only command as to
            |prevent spam.
        """

        expectedArgs = arrayOf(TrRest())
        execute { send(it.get<String>(0)) }
    }

    fun sebd() = command("sebd") {
        description = "Sends an embed. Only my owner can use this."
        aliases = arrayOf("sendembed")

        ownerOnly = true
        deleteSender = true

        extDescription = """
            |`$name title description [color]`\n
            |Sends a message embed to the command user's channel. This is an owner only command as
            |to prevent spam. For more advanced usage, it is advised to use the `ex` command.
        """

        expectedArgs = arrayOf(TrWord(), TrWord(), TrInt(true, EMBED_COLOR))
        execute { args ->
            val titleText = args.get<String>(0)
            val descriptionText = args.get<String>(1)
            val embedColor = args.get<Int>(2)

            send(
                embed {
                    title = titleText
                    description = descriptionText
                    color = embedColor
                }
            )
        }
    }

    fun regex() = command("regex") {
        description = "Tests a regex against some cases."
        aliases = arrayOf("testregex", "regularexpression")
        ownerOnly = true

        extDescription = """
            |`$name regex cases...`\n
            |This command attempts to match the given strings in `cases` with the given `regex`.
            |It will report which ones matched and which didn't, and for those that matched, if
            |there were groups, it will report those as well. The regex syntax is from Java. This
            |command can only be used by my owner to prevent ReDOS attacks.
        """

        expectedArgs = arrayOf(TrWord(), TrSplit())
        execute { args ->
            val regex = try {
                args.get<String>(0).toRegex()
            } catch (e: PatternSyntaxException) {
                sendError("That regex isn't valid!")
                return@execute
            }
            val cases = args.get<List<String>>(1)

            send(
                embed {
                    title = "${Emoji.SCALES}  Testing regex **$regex**:"
                    for (case in cases) {
                        val match = regex.matchEntire(case)?.groupValues ?: "(no match)"
                        description += "\n**$case**: $match"
                    }
                }
            )
        }
    }

    fun shutdown() = command("shutdown") {
        description = "Shuts down the bot. Only my owner can use this."
        ownerOnly = true

        extDescription = """
            |`$name`\n
            |Shuts down the bot process. There is a roughly three second long period of time
            |between command usage and actual process termination. First, I wait two seconds and
            |call `shutdownNow` on my `JDA` instance. Then, I wait another second and terminate
            |myself. Tragic. This is owner only for obvious reasons.
        """

        execute {
            sendSuccess("Goodbye, world...")

            delay(2000)
            jda.shutdownNow()

            // Give JDA some time to shut down in case I'm in China with a 10 kb/s connection. Oh,
            // wait... I wouldn't be able to access Discord without a VPN anyway.
            delay(1000)
            exitProcess(0)
        }
    }

    fun file() = command("file") {
        description = "Sends the contents of a file. Only my owner can use this."
        aliases = arrayOf("showfile")
        ownerOnly = true

        extDescription = """
            |`$name filename [-ru]`\n
            |This command sends the contents of or uploads a file. If the `-r` flag is provided, I
            |will treat `filename` as a relative path. If the `-u` flag is provided, I will upload
            |the file instead of sending its contents to the channel. This command can only be used
            |by my owner for obvious security reasons.
        """

        expectedArgs = arrayOf(TrWord(), TrWord(true))
        execute { args ->
            val filename = args.get<String>(0)
            val flags = args.get<String>(1)
            send(FileContentSender(filename, flags))
        }
    }

    fun ex() = command("ex") {
        description = "Executes arbitrary code. Only my owner can use this."
        aliases = arrayOf("exec", "execute")

        ownerOnly = true
        noArgParsing = true

        extDescription = """
            |`$name code`\n
            |Executes Kotlin code in an unconstrained environment. This command can only be used by
            |my owner, for obvious security reasons. The only available global is `ctx`, the
            |`CommandContext` object associated with the current command execution. The event and
            |bot objects can be accessed from the command context. Note that star/wildcard imports
            |are not supported because I am lazy.
        """

        expectedArgs = arrayOf(TrRest())
        execute { args ->
            val code = args.get<String>(0)
            var language: String

            val script = code
                .removeSurrounding("```")
                .also { language = it.substringBefore("\n") }
                .substringAfter("\n")

            val result = when (language) {
                "kotlin" -> KotlinExecutor(script.split("\n")).execute(this)
                "sh" -> ShellScriptExecutor(script).execute(this)
                else -> {
                    sendError("You must specify a valid language in a code block!")
                    return@execute
                }
            }
            send(ExecResultSender(result))
        }
    }
}
