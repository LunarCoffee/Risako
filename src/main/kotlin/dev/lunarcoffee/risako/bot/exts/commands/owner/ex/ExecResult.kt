package dev.lunarcoffee.risako.bot.exts.commands.owner.ex

class ExecResult(
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
