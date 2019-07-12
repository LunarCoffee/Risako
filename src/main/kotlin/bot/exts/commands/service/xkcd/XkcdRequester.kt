package bot.exts.commands.service.xkcd

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import bot.consts.GSON
import framework.core.std.Requester

internal class XkcdRequester(private val which: Int?) : Requester<XkcdComic> {
    override suspend fun get(): XkcdComic {
        return if (which == 404) {
            // Accessing the API endpoint for 404 results in, well, a 404. Despite that, the comic
            // canonically exists as stated by Randall, so this is a special comic just for that.
            XkcdComic.COMIC_404
        } else {
            GSON.fromJson(
                Fuel.get(if (which != null) "$BASE/$which/info.0.json" else "$BASE/info.0.json")
                    .awaitStringResult()
                    .get(),
                XkcdComic::class.java
            )
        }
    }

    companion object {
        private const val BASE = "https://xkcd.com"
    }
}
