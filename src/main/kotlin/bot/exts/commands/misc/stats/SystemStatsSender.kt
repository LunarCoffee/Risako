package bot.exts.commands.misc.stats

import bot.consts.Emoji
import framework.api.dsl.embed
import framework.api.extensions.send
import framework.core.commands.CommandContext
import framework.core.std.ContentSender

internal class SystemStatsSender(private val stats: SystemStats) : ContentSender {
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
