package dev.lunarcoffee.risako.bot.exts.commands.service.iss.image

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitByteArrayResult
import dev.lunarcoffee.risako.bot.consts.RISAKO_CONFIG
import dev.lunarcoffee.risako.bot.exts.commands.service.iss.stats.IssStats
import dev.lunarcoffee.risako.framework.core.std.Requester

class IssMapImageRequester(private val stats: IssStats) : Requester<ByteArray> {
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
