package dev.lunarcoffee.risako.bot.exts.commands.owner.ex.executors

import dev.lunarcoffee.risako.bot.exts.commands.owner.ex.ExecResult
import dev.lunarcoffee.risako.framework.api.extensions.*
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.system.measureNanoTime

class PythonExecutor(private val script: String): CodeExecutor {
    override suspend fun execute(ctx: CommandContext): ExecResult {
        File("$SCRIPT_ROOT/script.py").writeText("#!/usr/bin/python3.7\n$script")

        var process: Process? = null
        val fileOut = File("$SCRIPT_ROOT/out.txt")
        val fileErr = File("$SCRIPT_ROOT/err.txt")

        val time = measureNanoTime {
            try {
                process = ProcessBuilder("python3.7", "$SCRIPT_ROOT/script.py")
                    .redirectOutput(ProcessBuilder.Redirect.to(fileOut))
                    .redirectError(ProcessBuilder.Redirect.to(fileErr))
                    .start()
                    .apply { waitFor(10, TimeUnit.SECONDS) }
            } catch (e: IOException) {
                ctx.sendError("Error starting process! Check your PMs for details.")
                ctx.event.author.openPrivateChannel().await().send("```$e```")

                return ExecResult.ERROR
            }
        } / 1_000_000

        val stdout = "\n${fileOut.readText().trim()}".ifBlank { "" }
        val stderr = "\n${fileErr.readText().trim()}".ifBlank { "" }

        return ExecResult(
            "Python 3.7.3",
            stdout,
            stderr,
            process!!.exitValue(),
            time
        )
    }

    companion object {
        const val SCRIPT_ROOT = "src/main/resources/ex/py"
    }
}
