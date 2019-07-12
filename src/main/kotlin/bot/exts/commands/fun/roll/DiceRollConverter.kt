package bot.exts.commands.`fun`.roll

private val MATCH_REGEX = """(\d*)d(\d+)([+-]\d+)?""".toRegex()

fun String.toDiceRoll(): DiceRoll {
    val (times, sides, mod) = MATCH_REGEX.matchEntire(this)?.destructured
        ?: throw IllegalArgumentException()

    return DiceRoll(
        if (times.isBlank()) 1 else times.toInt(),
        sides.toInt(),
        if (mod.isBlank()) 0 else mod.toInt()
    )
}
