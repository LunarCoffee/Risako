package dev.lunarcoffee.risako.bot.exts.commands.misc.stats

import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.ContentSender

class SystemStatsSender(private val stats: SystemStats) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.send(
            embed {
                stats.run {
                    val creatorTag = ctx.jda.getUserById(ctx.bot.config.ownerId)!!.asTag

                    title = "${Emoji.LAPTOP_COMPUTER}  System statistics:"
                    description = """
                        |**Memory usage**: ${totalMemory - freeMemory}/$totalMemory MB
                        |**Language**: $language
                        |**Creator**: $creatorTag
                        |**JVM version**: $jvmVersion
                        |**Operating system**: $osName
                        |**Uptime**: $uptime
                        |**CPU architecture**: $cpuArchitecture
                        |**Logical cores available**: $logicalProcessors
                        |**Total threads**: $totalThreads
                        |**Running threads**: $runningThreads
                    """.trimMargin()
                }
            }
        )
    }
}
