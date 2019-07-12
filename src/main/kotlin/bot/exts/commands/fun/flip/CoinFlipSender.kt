package bot.exts.commands.`fun`.flip

import bot.consts.Emoji
import framework.api.dsl.embed
import framework.api.dsl.embedPaginator
import framework.api.extensions.send
import framework.core.commands.CommandContext
import framework.core.std.ContentSender
import kotlin.random.Random

internal class CoinFlipSender(private val times: Int) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        val flips = (1..times).map { if (Random.nextBoolean()) "heads" else "tails" }

        val heads = flips.count { it == "heads" }
        val tails = flips.count { it == "tails" }

        val result = if (times == 1) flips[0] else "$heads heads and $tails tails"

        ctx.send(
            ctx.embedPaginator {
                for (results in flips.chunked(100)) {
                    page(
                        embed {
                            title = "${Emoji.RADIO_BUTTON}  You flipped $result!"
                            description = results.toString()
                        }
                    )
                }
            }
        )
    }
}
