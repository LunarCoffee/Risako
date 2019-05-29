package dev.lunarcoffee.risako.bot.exts.commands.utility.help

import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.framework.api.dsl.EmbedDsl
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.core.commands.Command
import dev.lunarcoffee.risako.framework.core.commands.CommandContext

internal class HelpTextGenerator(private val ctx: CommandContext) {
    suspend fun sendListEmbed() {
        ctx.send(
            embed {
                title = "${Emoji.PAGE_FACING_UP}  All commands:"

                for (c in ctx.bot.commands.distinctBy { it.groupName }) {
                    // Hide owner-only commands unless the command user is the owner.
                    if (c.groupName != "Owner" || ctx.event.author.id == ctx.bot.config.ownerId) {
                        val names = ctx
                            .bot
                            .commands
                            .filter { it.groupName == c.groupName }
                            .map { it.name }
                            .sorted()
                        description += "**${c.groupName}**: $names\n"
                    }
                }

                footer { text = "Type '..help help' for more info." }
            }
        )
    }

    suspend fun sendDetailedHelpEmbed(command: Command, flags: String) {
        ctx.send(
            embed {
                // The first line of the extended description should always be the command usage
                // (i.e. `help [command name] [-v]`).
                val usage = command.extDescription.substringBefore("\n")
                val aliases = if (command.aliases.isEmpty()) {
                    "(none)"
                } else {
                    command.aliases.toList().toString()
                }

                title = "${Emoji.PAGE_FACING_UP}  Info on **${command.name}**:"
                description = """
                    |**Aliases**: $aliases
                    |**Description**: ${command.description}
                    |**Usage**: $usage
                """.trimMargin()

                if (flags != "-v") {
                    footer { text = "Type '..help ${command.name} -v' for more info." }
                } else {
                    addExtendedDescription(command)
                }
            }
        )
    }

    private fun EmbedDsl.addExtendedDescription(command: Command) {
        // The "Extended description" group is always first.
        val extDescription = " &{Extended description:}" +
                command.extDescription.substringAfter("\n")

        val fieldContents = extDescription
            .split(singleField)
            .drop(1)
            .iterator()

        // Turn "&{name}" into a field with name [name] and the content of the part below the tag
        // (until the next field tag).
        val matcher = singleField.toPattern().matcher(extDescription)
        val descriptionFields = mutableMapOf<String, String>()
        while (matcher.find()) {
            descriptionFields[matcher.group(1)] = fieldContents.next()
        }

        for ((fName, fContent) in descriptionFields) {
            field {
                name = fName
                content = fContent
            }
        }
    }

    companion object {
        private val singleField = """&\{([^{}]+)}""".toRegex()
    }
}
