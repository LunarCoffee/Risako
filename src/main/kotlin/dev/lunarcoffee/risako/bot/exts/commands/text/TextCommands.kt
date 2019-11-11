@file:Suppress("unused")

package dev.lunarcoffee.risako.bot.exts.commands.text

import dev.lunarcoffee.risako.framework.api.dsl.command
import dev.lunarcoffee.risako.framework.api.extensions.sendError
import dev.lunarcoffee.risako.framework.api.extensions.sendSuccess
import dev.lunarcoffee.risako.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.commands.transformers.*
import kotlin.random.Random

@CommandGroup("Text")
class TextCommands(private val bot: Bot) {
    fun repeat() = command("repeat") {
        description = "Repeats some text a certain number of times."
        aliases = arrayOf("repstr", "repeatstring")

        extDescription = """
            |`$name times text`\n
            |Repeats `text` for `times` times in a row. Simple as that.
        """

        expectedArgs = arrayOf(TrInt(), TrRest())
        execute { args ->
            val times = args.get<Int>(0)
            val text = args.get<String>(1)

            sendSuccess("Your repeated text is `${text.repeat(times)}`.")
        }
    }

    fun rev() = command("rev") {
        description = "Reverses the given text."
        aliases = arrayOf("reverse", "backwards")

        extDescription = """
            |`$name text [-w]`\n
            |Reverses the given text, letter by letter if the `-w` flag is not specified, and word
            |by word if it is specified (the text is simply split by spaces).
        """

        expectedArgs = arrayOf(TrRest())
        execute { args ->
            val rawText = args.get<String>(0)
            val byWords = rawText.endsWith(" -w")

            val text = if (byWords)
                rawText.split(" ").dropLast(1).reversed().joinToString(" ")
            else
                rawText.reversed()

            sendSuccess("Your text reversed is `$text`")
        }
    }

    fun len() = command("len") {
        description = "Shows the length of the given text."
        aliases = arrayOf("length")

        extDescription = """
            |`$name text [-w]`\n
            |Counts the characters in the given text if the `-w` flag is not specified, and counts
            |words if it is specified (the text is simply split by spaces).
        """

        expectedArgs = arrayOf(TrRest())
        execute { args ->
            val rawText = args.get<String>(0)
            val byWords = rawText.endsWith(" -w")

            val length = if (byWords) rawText.split(" ").size - 1 else rawText.length
            val charsOrWords = if (byWords) "words" else "characters"

            sendSuccess("Your text is `$length` $charsOrWords long.")
        }
    }

    fun sub() = command("sub") {
        description = "Performs simple substitutions on some text."
        aliases = arrayOf("replace", "substitute")

        extDescription = """
            |`$name text input=output...`\n
            |Replaces every occurrence of every `input` with its corresponding `output`. For
            |example, for the command invocation `..sub hwllaa w=e aa=o`, the result would be
            |`hello`. If you need to replace an equals sign in the text, don't put anything on the
            |left side (`=a` would replace all equals signs with `a`).
            |&{Removing characters or phrases:}
            |This command can also remove any strings of text. To do this, simply do not provide
            |an `output` for the `input` you want to remove. For example, `..sub helylyo y=` will
            |remove every `y` from the text.
            |&{Substitution order:}
            |It is worth mentioning that the order in which the substitutions will be applied is
            |from the first provided to the last. This is important because an invocation like
            |`..sub holle e=o o=e` will not result in `hello`, but rather `helle`.
        """

        expectedArgs = arrayOf(TrWord(), TrRemaining())
        execute { args ->
            val text = args.get<String>(0)
            val substitutions = args
                .get<List<String>>(1)
                .map { it.split("=") }
                .filter { it.size > 1 }
                .map { Pair(it[0].ifEmpty { "=" }, it[1]) }

            val result = substitutions
                .fold(text) { acc, next -> acc.replace(next.first, next.second) }

            val resultText = if (result.isEmpty()) "empty" else "`$result`"
            sendSuccess("Your substituted text is $resultText.")
        }
    }

    fun randcase() = command("randcase") {
        description = "Randomizes the casing of the letters in some text."
        aliases = arrayOf("sarcastify")

        extDescription = """
            |`$name text`\n
            |Randomizes the case of every letter in `text`, sometimes making it look sarcastic.
            |This might not work for some languages where casing works differently than in English.
        """

        expectedArgs = arrayOf(TrRest())
        execute { args ->
            val text = args.get<String>(0)
            val randomized = text
                .map { if (Random.nextBoolean()) it.toUpperCase() else it.toLowerCase() }
                .joinToString("")

            sendSuccess("Your randomized text is `$randomized`.")
        }
    }

    fun split() = command("split") {
        description = "Splits a string in one two ways."
        aliases = arrayOf("chunkify")

        extDescription = """
            |`$name n=number|d=delimiter text`\n
            |Splits a string depending on what argument is received. If it looks like `n=8` (where
            |the `8` can be any positive number), the string will be split every `8` characters. If
            |the argument looks like `d=,` on the other hand, it will be split every time the comma
            |appears.
        """

        expectedArgs = arrayOf(TrWord(), TrRest())
        execute { args ->
            val chunkOrDelimiter = args.get<String>(0)
            val text = args.get<String>(1)

            when {
                chunkOrDelimiter.startsWith("n=") -> {
                    val chunkSize = chunkOrDelimiter.drop(2).toIntOrNull()
                    if (chunkSize == null || chunkSize < 1) {
                        sendError("The chunk size must be greater than zero!")
                        return@execute
                    }
                    sendSuccess("Your split text is ${text.chunked(chunkSize).map { "`$it`" }}")
                }
                chunkOrDelimiter.startsWith("d=") -> {
                    val delimiter = chunkOrDelimiter.drop(2)
                    if (delimiter.isEmpty()) {
                        sendError("The delimiter cannot be blank!")
                        return@execute
                    }
                    sendSuccess("Your split text is ${text.split(delimiter).map { "`$it`" }}!")
                }
                else -> sendError("That's not quite right. Type `..help split` for details.")
            }
        }
    }
}
