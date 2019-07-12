package bot.exts.commands.service.osu

internal abstract class OsuHasMode {
    abstract var mode: Int

    val modeUrl
        get() = when (mode) {
            0 -> "osu"
            1 -> "taiko"
            2 -> "fruits"
            3 -> "mania"
            else -> throw IllegalStateException()
        }

    val modeName
        get() = when (mode) {
            0 -> "normal"
            1 -> "taiko"
            2 -> "catch"
            3 -> "mania"
            else -> throw IllegalStateException()
        }
}
