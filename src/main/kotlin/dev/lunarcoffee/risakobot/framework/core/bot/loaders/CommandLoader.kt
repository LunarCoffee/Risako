package dev.lunarcoffee.risakobot.framework.core.bot.loaders

import dev.lunarcoffee.risakobot.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risakobot.framework.core.bot.Bot
import dev.lunarcoffee.risakobot.framework.core.commands.Command

internal class CommandLoader(override val bot: Bot) : ComponentClassLoader() {
    private val commandGroups = loadClasses(bot.config.commandP)
        .filter { c -> c.annotations.any { it.annotationClass == CommandGroup::class } }

    // Map of [CommandGroups] to their commands.
    val groupToCommands = commandGroups
        .map { group -> group.methods.filter { it.returnType == Command::class.java } }
        .zip(commandGroups.map { callConstructorWithBot(it)!! })
        .associate { (methods, group) ->
            val annotation = group::class.annotations.find { it is CommandGroup } as CommandGroup
            annotation to methods.map {
                (it.invoke(group) as Command).apply { groupName = annotation.name }
            }
        }

    val commands = groupToCommands.values.flatten().toMutableList()
}