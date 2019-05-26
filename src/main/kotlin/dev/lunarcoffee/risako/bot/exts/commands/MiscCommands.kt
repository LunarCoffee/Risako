@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.commands

import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.framework.api.dsl.command
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.extensions.*
import dev.lunarcoffee.risako.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.commands.transformers.TrInt
import dev.lunarcoffee.risako.framework.core.commands.transformers.TrWord
import dev.lunarcoffee.risako.framework.core.trimToDescription
import java.io.File
import java.security.SecureRandom
import kotlin.random.Random
import kotlin.system.measureNanoTime

@CommandGroup("Misc")
internal class MiscCommands(private val bot: Bot) {
    fun ping() = command("ping") {
        description = "Gets various info about how good I feel am today."
        aliases = arrayOf("pong", "peng", "latency")

        extDescription = """
            |`$name`\n
            |This command shows three values: the heartbeat latency, REST API latency, and stack
            |allocation latency (this one's just for fun and might not be accurate either).
        """

        execute = { ctx, _ ->
            val apiLatency = ctx.jda.restPing.await()
            val stackLatency = measureNanoTime {
                @Suppress("UNUSED_VARIABLE")
                val temp = 0
            }

            ctx.send(
                embed {
                    title = "${Emoji.PING_PONG}  Pong!"
                    description = "[${ctx.jda.gatewayPing}ms, ${apiLatency}ms, ${stackLatency}ns]"
                }
            )
        }
    }

    fun loc() = command("loc") {
        description = "Gets stats and info about my code!"
        aliases = arrayOf("linesofcode")

        extDescription = """
            |`$name`\n
            |This command shows various stats about my code, like how many lines I'm made of, the
            |number of files and folders I am, and the number of characters I'm written in.
        """

        execute = { ctx, _ ->
            val files = mutableListOf<File>()
            var dirs = 0

            File(ctx.bot.config.sourceRootDir).walk().forEach {
                if (it.extension == "kt") {
                    files += it
                } else if (it.isDirectory) {
                    dirs++
                }
            }

            var linesOfCode = 0
            var blankLines = 0
            var characters = 0

            files.asSequence().flatMap { it.readLines().asSequence() }.forEach {
                if (it.isBlank()) {
                    blankLines++
                }
                linesOfCode++
                characters += it.length
            }

            ctx.send(
                embed {
                    title = "${Emoji.OPEN_FILE_FOLDER}  Code statistics:"
                    description = """
                        **Lines of code**: $linesOfCode
                        **Lines with content**: ${linesOfCode - blankLines}
                        **Blank lines**: $blankLines
                        **Characters**: $characters
                        **Code files**: ${files.count()}
                        **Directories**: $dirs
                    """.trimIndent()
                }
            )
        }
    }

    fun rng() = command("rng") {
        val secureRandom = SecureRandom()

        description = "Gets you a random number between two numbers (inclusive)."
        aliases = arrayOf("rand", "random")

        extDescription = """
            |`$name low high [-s]`\n
            |This command generates a random number within the closed interval [`low`, `high`]. If
            |the `-s` flag is set, a secure source of randomness will be used.
        """.trimToDescription()

        expectedArgs = arrayOf(TrInt(), TrInt(), TrWord(true))
        execute = { ctx, args ->
            val lowerBound = args.get<Int>(0)
            val upperBound = args.get<Int>(1) + 1
            val secure = args.get<String>(2) == "-s"

            val number = if (secure) {
                secureRandom.nextInt(upperBound - lowerBound) + lowerBound
            } else {
                Random.nextInt(lowerBound, upperBound)
            }

            val secureText = if (secure) " secure" else ""
            ctx.success("Your$secureText random number is `$number`!")
        }
    }

    fun git() = command("git") {
        description = "Gets my GitLab repo URL."
        aliases = arrayOf("repo", "github")

        extDescription = """
            |`$name`\n
            |Unless you are a developer, this command probably has no use. My code is licensed
            |under the MIT license.
        """.trimToDescription()

        execute = { ctx, _ ->
            ctx.success("<https://gitlab.com/LunarCoffee/risako>")
        }
    }
}
