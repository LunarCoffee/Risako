package dev.lunarcoffee.risako.bot.exts.commands.service.osu.beatmap

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.google.gson.GsonBuilder
import dev.lunarcoffee.risako.bot.consts.GSON
import dev.lunarcoffee.risako.bot.consts.RISAKO_CONFIG
import dev.lunarcoffee.risako.framework.core.std.Requester

class OsuBeatmapRequester(
    private val id: String,
    private val mode: Int
) : Requester<List<OsuBeatmapInfo>?> {

    override suspend fun get(): List<OsuBeatmapInfo>? {
        val beatmaps = GSON
            .fromJson(
                Fuel.get(
                    "https://osu.ppy.sh/api/get_beatmaps",
                    listOf(
                        "k" to RISAKO_CONFIG.osuToken,
                        "s" to id,
                        "m" to mode
                    )
                ).awaitStringResult().get(),
                ArrayList<Map<String, Any>>().javaClass
            )
            .map { OSU_BEATMAP_GSON.fromJson(GSON.toJson(it), OsuBeatmapInfo::class.java) }
            .onEach { it.mode = mode }
            .sortedBy { it.starRating.toDouble() }

        return if (beatmaps.isEmpty()) null else beatmaps
    }

    companion object {
        private val OSU_BEATMAP_GSON = GsonBuilder()
            .setFieldNamingStrategy(OsuBeatmapStrategy())
            .create()
    }
}
