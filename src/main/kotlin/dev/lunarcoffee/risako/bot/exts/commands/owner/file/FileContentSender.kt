package dev.lunarcoffee.risako.bot.exts.commands.owner.file

import dev.lunarcoffee.risako.bot.consts.RISAKO_CONFIG
import dev.lunarcoffee.risako.framework.api.dsl.messagePaginator
import dev.lunarcoffee.risako.framework.api.extensions.*
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.silence
import dev.lunarcoffee.risako.framework.core.std.ContentSender
import java.io.File

internal class FileContentSender(
    private val filename: String,
    flags: String
) : ContentSender {

    private var upload = false
    private var rawPath = false
    private lateinit var apiTokens: Array<String>

    init {
        when (flags) {
            "-r" -> rawPath = true
            "-u" -> upload = true
            "-ru", "-ur" -> {
                upload = true
                rawPath = true
            }
        }
    }

    override suspend fun send(ctx: CommandContext) {
        // Having a raw path allows for getting non-bot files.
        val file = if (rawPath) {
            silence { File(filename) }
        } else {
            File(".").walk().find { it.name.equals(filename, true) }
        }

        if (file == null) {
            ctx.sendError("I can't find a file with that name!")
            return
        }

        if (checkTokenLeaks(file, ctx)) {
            ctx.sendError("I can't upload that file! Try again without the `-u` flag.")
            return
        }

        if (upload) {
            ctx.sendMessage(":white_check_mark:  Your file is here!  **\\o/**")
                .addFile(file)
                .await()
        } else {
            ctx.send(
                messagePaginator(ctx.event.author) {
                    // We use this to replace occurrences of API tokens in each page.
                    val tokenRegex = "(${apiTokens.joinToString("|")})".toRegex()

                    file.readLines().chunked(16).map { it.joinToString("\n") }.forEach {
                        val language = when (file.extension) {
                            "kt" -> "kotlin"
                            "yaml", "py" -> file.extension
                            else -> ""
                        }
                        val cleaned = it.replace(tokenRegex, "[REDACTED]")

                        page("```$language\n$cleaned```")
                    }
                }
            )
        }
    }

    private fun checkTokenLeaks(file: File, ctx: CommandContext): Boolean {
        val rawFileText = file.readText()
        apiTokens = arrayOf(
            ctx.bot.config.token,
            RISAKO_CONFIG.mapboxToken,
            RISAKO_CONFIG.osuToken
        )

        // Don't leak an API token by uploading a file with it.
        return apiTokens.any { it in rawFileText } && upload
    }
}
