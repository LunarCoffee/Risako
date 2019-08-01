package dev.lunarcoffee.risakobot.bot.exts.commands.owner.ex

internal class ExecResult(
    val header: String,
    val stdout: String,
    val stderr: String,
    val result: Any?,
    val time: Long
) {
    companion object {
        val ERROR = ExecResult("", "", "", null, -1)
    }
}
