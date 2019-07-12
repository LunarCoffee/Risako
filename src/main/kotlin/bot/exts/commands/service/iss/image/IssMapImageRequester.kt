package bot.exts.commands.service.iss.image

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitByteArrayResult
import bot.consts.RISAKO_CONFIG
import bot.exts.commands.service.iss.stats.IssStats
import framework.core.std.Requester

internal class IssMapImageRequester(private val stats: IssStats) : Requester<ByteArray> {
    override suspend fun get(): ByteArray {
        val args = "${stats.longitude},${stats.latitude},3/800x800"
        return Fuel
            .get("$BASE/$args", listOf("access_token" to RISAKO_CONFIG.mapboxToken))
            .awaitByteArrayResult()
            .get()
    }

    companion object {
        private const val BASE = "https://api.mapbox.com/styles/v1/mapbox/streets-v11/static"
    }
}
