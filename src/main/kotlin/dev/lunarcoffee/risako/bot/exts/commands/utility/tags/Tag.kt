package dev.lunarcoffee.risako.bot.exts.commands.utility.tags

import java.util.*

class Tag(
    val guildId: String,
    val authorId: String,
    val name: String,
    var content: String,
    val timeCreated: Date
) {
    val textContent get() = content.removeSuffix(attachments)

    val attachments
        get() = content
            .split("\n")
            .takeLastWhile { it.startsWith("https://cdn.discordapp.com/attachments/") }
            .joinToString("\n")

    val namedAttachments
        get() = attachments
            .split("\n")
            .joinToString("\n") { "[${extractFileName(it)}]($it)" }

    private fun extractFileName(url: String): String {
        return url.substringAfter("s/").substringAfter("/").substringAfter("/")
    }
}
