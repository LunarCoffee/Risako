@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.commands.misc

import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.bot.exts.commands.misc.loc.CodeStats
import dev.lunarcoffee.risako.bot.exts.commands.misc.loc.CodeStatsSender
import dev.lunarcoffee.risako.bot.exts.commands.misc.stats.SystemStats
import dev.lunarcoffee.risako.bot.exts.commands.misc.stats.SystemStatsSender
import dev.lunarcoffee.risako.framework.api.dsl.command
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.extensions.*
import dev.lunarcoffee.risako.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.commands.transformers.TrInt
import dev.lunarcoffee.risako.framework.core.commands.transformers.TrWord
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

        execute {
            val apiLatency = jda.restPing.await()
            val stackLatency = measureNanoTime {
                @Suppress("UNUSED_VARIABLE")
                val temp = 0
            }

            send(
                embed {
                    title = "${Emoji.PING_PONG}  Pong!"
                    description = "[${jda.gatewayPing}ms, ${apiLatency}ms, ${stackLatency}ns]"
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

    fun loc() = command("loc") {
        description = "Gets stats and info about my code!"
        aliases = arrayOf("linesofcode")

        extDescription = """
            |`$name`\n
            |This command shows various stats about my code, like how many lines I'm made of, the
            |number of files and folders I am, and the number of characters I'm written in.
        """

        execute { CodeStatsSender(CodeStats(bot)).send(this) }
    }

    fun git() = command("git") {
        description = "Gets my GitLab repo URL."
        aliases = arrayOf("repo", "github")

        extDescription = """
            |`$name`\n
            |Unless you are a developer, this command probably has no use. My code is licensed
            |under the MIT license.
        """

        execute { sendSuccess("<https://gitlab.com/LunarCoffee/risako>") }
    }

    fun stats() = command("stats") {
        description = "Gets various stats about my... existence?"
        aliases = arrayOf("statistics")

        extDescription = """
            |`$name`\n
            |Gets various stats about me, like how much RAM I'm eating up, the language I'm written
            |in, how long I've been awake, and what architecture the CPU I'm running on is.
        """

        execute { SystemStatsSender(SystemStats()).send(this) }
    }
}
