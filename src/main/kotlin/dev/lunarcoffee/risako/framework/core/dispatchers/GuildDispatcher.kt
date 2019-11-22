package dev.lunarcoffee.risako.framework.core.dispatchers

import dev.lunarcoffee.risako.framework.api.extensions.await
import dev.lunarcoffee.risako.framework.api.extensions.sendError
import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.commands.Command
import dev.lunarcoffee.risako.framework.core.commands.GuildCommandArgs
import dev.lunarcoffee.risako.framework.core.commands.GuildCommandContext
import dev.lunarcoffee.risako.framework.core.dispatchers.parsers.ArgParser
import dev.lunarcoffee.risako.framework.core.std.OpError
import dev.lunarcoffee.risako.framework.core.std.OpSuccess
import kotlinx.coroutines.*
import mu.KotlinLogging
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class GuildDispatcher(
    override val bot: Bot,
    override val argParser: ArgParser
) : CoroutineScope by CoroutineScope(Dispatchers.IO), Dispatcher {

    override val commands = mutableListOf<Command>()

    override fun addCommand(command: Command) {
        commands += command
    }

    override fun registerAllCommands() {
        bot.jda.removeEventListener(this)
        bot.jda.addEventListener(this)
    }

    override suspend fun handleEvent(event: GenericEvent) {
        if (
            event !is MessageReceivedEvent ||
            !event.message.contentRaw.startsWith(bot.config.prefix) ||
            event.author.isBot ||
            !event.isFromGuild ||
            event.isWebhookMessage
        ) {
            log.debug { "Event ${event.javaClass.name} caught and ignored!" }
            return
        }

        val content = event.message.contentRaw.replace("\u200B", "")
        val channel = event.textChannel
        val authorName = event.author.name

        val commandName = content.substringAfter(bot.config.prefix).substringBefore(" ")
        val command = commands.find { commandName in it.names } ?: return

        if (command.ownerOnly && event.author.id != bot.config.ownerId) {
            channel.sendError("Only my owner can use that command!")
            log.info { "[OWNER] $authorName tried to use command `$commandName`!" }
            return
        }

        if (command.nsfwOnly && !channel.isNSFW) {
            channel.sendError("You need to be in an NSFW channel to use that command!")
            log.info { "[NNSFW] $authorName tried to use command `$commandName`!" }
            return
        }

        val commandContext = GuildCommandContext(event, bot)
        val transformerArgs = if (command.noArgParsing)
            mutableListOf(content.substringAfter("$commandName "))
        else
        // List of space separated words (unless a phrase is wrapped in double quotes).
            argParser.parseArgs(content).toMutableList()

        // Arguments that will be wrapped in a [CommandArgs] object and be passed to the [execute]
        // lambda of [command].
        val commandArgs = command.expectedArgs.map { transformer ->
            when (val transformed = transformer.transform(commandContext, transformerArgs)) {
                is OpSuccess -> transformed.result
                is OpError -> {
                    sendUsage(channel, commandName)
                    log.info { "[IARGS] $authorName tried to use command `$commandName`!" }
                    return
                }
            }
        }

        // If [transformerArgs] isn't empty, that means extra arguments were provided, which
        // shouldn't be allowed.
        if (transformerArgs.isNotEmpty()) {
            sendUsage(channel, commandName)
            return
        }

        if (command.deleteSender)
            event.message.delete().await()

        command.dispatch(commandContext, GuildCommandArgs(commandArgs))
        log.info { "$authorName used command `$commandName` with args $commandArgs" }
    }

    override fun onEvent(event: GenericEvent) {
        launch {
            try {
                handleEvent(event)
            } catch (e: Exception) {
                log.warn("Exception caught at the dispatcher level:")
                e.printStackTrace()
            }
        }
    }

    // Tell the user to see the command's help message if the arguments were wrong.
    private suspend fun sendUsage(channel: MessageChannel, name: String) {
        channel.sendError("That's not quite the right usage. Type `..help $name` for more info.") {
            delay(5000L)
            it.delete().await()
        }
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
