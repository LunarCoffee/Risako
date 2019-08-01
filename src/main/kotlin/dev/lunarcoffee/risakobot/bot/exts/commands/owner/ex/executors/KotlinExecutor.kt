package dev.lunarcoffee.risakobot.bot.exts.commands.owner.ex.executors

import dev.lunarcoffee.risakobot.bot.exts.commands.owner.ex.ExecResult
import dev.lunarcoffee.risakobot.framework.api.extensions.*
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import kotlinx.coroutines.*
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import java.io.*
import javax.script.*

internal class KotlinExecutor(private val lines: List<String>) : CodeExecutor {
    override suspend fun execute(ctx: CommandContext): ExecResult {
        // Import statements in the code. These will be prepended to the command prelude.
        val imports = lines
            .filter { it.matches(IMPORT_STATEMENT) }
            .joinToString(";")

        // Remove import statements (the command prelude contains a timing mechanism that runs a
        // lambda, in which you cannot have import statements for some reason).
        val code = lines
            .filter { !it.matches(IMPORT_STATEMENT) }
            .joinToString("\n")

        // Redirect stdout and stderr so we can keep what is outputted.
        val tempStdout = ByteArrayOutputStream().also { System.setOut(PrintStream(it)) }
        val tempStderr = ByteArrayOutputStream().also { System.setErr(PrintStream(it)) }

        val (time, result) = try {
            withContext(Dispatchers.Default) {
                ENGINE.eval(
                    // This prepends imports and adds the actual code.
                    File("$EX_ROOT/ek_prelude.txt").readText().format(imports, code),
                    SimpleBindings().apply { put("event", ctx.event) }
                ) as Pair<*, *>
            }
        } catch (e: ScriptException) {
            ctx.sendError("Error during execution! Check your PMs for details.")
            ctx.event.author.openPrivateChannel().await().send("```kotlin\n$e```")

            return ExecResult.ERROR
        } catch (e: TimeoutCancellationException) {
            ctx.sendError("Execution took too long!")

            return ExecResult.ERROR
        } finally {
            // Reset stdout and stderr.
            System.setOut(PrintStream(FileOutputStream(FileDescriptor.out)))
            System.setErr(PrintStream(FileOutputStream(FileDescriptor.err)))
        }

        val stdout = if (tempStdout.size() > 0) "\n${tempStdout.toString().trim()}" else ""
        val stderr = if (tempStderr.size() > 0) "\n${tempStderr.toString().trim()}" else ""

        return ExecResult(
            "JSR223 Kotlin Scripting Engine (1.3.21)",
            stdout,
            stderr,
            result,
            time as Long
        )
    }

    companion object {
        private const val EX_ROOT = "src/main/resources/ex"

        private val IMPORT_STATEMENT = """^\s*import\s+([A-z0-9]+\.)*[A-z0-9]+""".toRegex()
        private val ENGINE = ScriptEngineManager()
            .getEngineByName("kotlin")!!
            .also { setIdeaIoUseFallback() }
    }
}
