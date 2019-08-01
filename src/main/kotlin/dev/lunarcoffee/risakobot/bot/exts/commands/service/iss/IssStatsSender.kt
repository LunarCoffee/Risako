package dev.lunarcoffee.risakobot.bot.exts.commands.service.iss

import dev.lunarcoffee.risakobot.bot.consts.Emoji
import dev.lunarcoffee.risakobot.bot.exts.commands.service.iss.image.IssMapSaver
import dev.lunarcoffee.risakobot.bot.exts.commands.service.iss.stats.IssStatsRequester
import dev.lunarcoffee.risakobot.framework.api.dsl.embed
import dev.lunarcoffee.risakobot.framework.api.extensions.await
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import dev.lunarcoffee.risakobot.framework.core.std.ContentSender

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
