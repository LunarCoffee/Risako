package dev.lunarcoffee.risakobot.bot.exts.commands.service.osu.user

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.google.gson.GsonBuilder
import dev.lunarcoffee.risakobot.bot.consts.RISAKO_CONFIG
import dev.lunarcoffee.risakobot.framework.core.std.Requester

internal class OsuUserRequester(
    private val usernameOrId: String,
    private val mode: Int
) : Requester<OsuUserInfo?> {

    override suspend fun get(): OsuUserInfo? {
        return try {
            OSU_USER_GSON.fromJson(
                Fuel.get(
                    "https://osu.ppy.sh/api/get_user",
                    listOf(
                        "k" to RISAKO_CONFIG.osuToken,
                        "u" to usernameOrId,
                        "m" to mode
                    )
                ).awaitStringResult().get().drop(1).dropLast(1),
                OsuUserInfo::class.java
            )!!
        } catch (e: NullPointerException) {
            null
        }
    }

    companion object {
        private val OSU_USER_GSON = GsonBuilder()
            .setFieldNamingStrategy(OsuUserStrategy())
            .create()
    }
}
