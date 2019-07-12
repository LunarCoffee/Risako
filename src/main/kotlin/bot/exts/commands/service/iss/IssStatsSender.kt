package bot.exts.commands.service.iss

import bot.consts.Emoji
import bot.exts.commands.service.iss.image.IssMapSaver
import bot.exts.commands.service.iss.stats.IssStatsRequester
import framework.api.dsl.embed
import framework.api.extensions.await
import framework.core.commands.CommandContext
import framework.core.std.ContentSender

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
