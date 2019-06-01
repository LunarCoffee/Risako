package dev.lunarcoffee.risako.bot.exts.commands.service.iss

import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.bot.exts.commands.service.iss.image.IssMapSaver
import dev.lunarcoffee.risako.bot.exts.commands.service.iss.stats.IssStatsRequester
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.extensions.await
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.ContentSender

internal object IssStatsSender : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        val stats = IssStatsRequester().get()
        val imageFile = IssMapSaver(stats).saveImage()

        ctx.sendMessage(
            embed {
                stats.run {
                    title = "${Emoji.SATELLITE}  Info on the ISS:"
                    description = """
                        |**Longitude**: $longitudeStr
                        |**Latitude**: $latitudeStr
                        |**Altitude**: $altitude km
                        |**Velocity**: $velocity km/h
                    """.trimMargin()
                }
                image { url = "attachment://${imageFile.name}" }
            }
        ).addFile(imageFile).await()
    }
}
