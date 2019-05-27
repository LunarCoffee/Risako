@file:Suppress("LeakingThis")

package dev.lunarcoffee.risako.framework.core.bot

import dev.lunarcoffee.risako.framework.core.bot.config.DefaultConfig
import dev.lunarcoffee.risako.framework.core.bot.loaders.CommandLoader
import dev.lunarcoffee.risako.framework.core.bot.loaders.ListenerLoader
import dev.lunarcoffee.risako.framework.core.commands.Command
import dev.lunarcoffee.risako.framework.core.dispatchers.Dispatcher
import dev.lunarcoffee.risako.framework.core.dispatchers.GuildDispatcher
import dev.lunarcoffee.risako.framework.core.dispatchers.parsers.DefaultArgParser
import dev.lunarcoffee.risako.framework.core.services.paginators.PaginationReactionListener
import dev.lunarcoffee.risako.framework.core.services.waiters.WaitList
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.yaml.snakeyaml.Yaml
import java.io.File

internal open class DefaultBot(configPath: String) : Bot {
    init {
        instance = this
    }

    final override val jda: JDA
    final override val dispatcher: Dispatcher

    final override val config = Yaml()
        .loadAs(File(configPath).readText(), DefaultConfig::class.java)!!

    private val commandLoader = CommandLoader(this)

    private val groupToCommands = commandLoader.groupToCommands
    override val commands = commandLoader.commands
    override val commandNames get() = commands.map { it.name }.sorted()

    private val listenerLoader = ListenerLoader(this)

    final override val listeners = listenerLoader.listeners
    override val listenerNames
        get() = listeners
            .map { it.javaClass.name.substringAfterLast(".") }
            .sorted()

    init {
        jda = JDABuilder().setToken(config.token).build()
        dispatcher = GuildDispatcher(this, DefaultArgParser())

        // Add all event listeners.
        jda.addEventListener(*listeners.toTypedArray(), PaginationReactionListener, WaitList)
        log.info {
            val groupNames = listeners.map { it.javaClass.name.substringAfterLast(".") }
            "Loaded listener groups: $groupNames"
        }
    }

    // This method adds a command dynamically, and is not meant to be used as a replacement for
    // annotating a class with [CommandGroup] and defining commands in it.
    override fun addCommand(command: Command) {
        commands += command
        dispatcher.apply {
            addCommand(command)
            registerAllCommands()
        }
    }

    // This method adds a listener dynamically, and is not meant to be used as a replacement for
    // annotating a class with a [ListenerGroup].
    override fun addListener(listener: EventListener) {
        listeners += listener as ListenerAdapter
        jda.addEventListener(listener)
    }

    override fun loadAllCommands() {
        groupToCommands
            .values
            .flatten()
            .forEach { dispatcher.addCommand(it) }
        dispatcher.registerAllCommands()
    }

    companion object {
        private val log = KotlinLogging.logger {}

        // This shouldn't be used outside of the <framework> package.
        internal lateinit var instance: Bot
    }
}
