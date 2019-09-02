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
import kotlin.system.measureNanoTime

@CommandGroup("Misc")
class MiscCommands(private val bot: Bot) {
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

    fun loc() = command("loc") {
        description = "Gets stats and info about my code!"
        aliases = arrayOf("linesofcode")

        extDescription = """
            |`$name`\n
            |This command shows various stats about my code, like how many lines I'm made of, the
            |number of files and folders I am, and the number of characters I'm written in.
        """

        execute { send(CodeStatsSender(CodeStats(bot))) }
    }

    fun git() = command("git") {
        description = "Gets my GitHub repo URL."
        aliases = arrayOf("repo", "github")

        extDescription = """
            |`$name`\n
            |Unless you are a developer, this command probably has no use. My code is licensed
            |under the MIT license.
        """

        execute { sendSuccess("<https://github.com/LunarCoffee/Risako>") }
    }

    fun stats() = command("stats") {
        description = "Gets various stats about my... existence?"
        aliases = arrayOf("statistics")

        extDescription = """
            |`$name`\n
            |Gets various stats about me, like how much RAM I'm eating up, the language I'm written
            |in, how long I've been awake, and what architecture the CPU I'm running on is.
        """

        execute { send(SystemStatsSender(SystemStats())) }
    }
}
