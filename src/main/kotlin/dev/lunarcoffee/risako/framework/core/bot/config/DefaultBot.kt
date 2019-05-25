package dev.lunarcoffee.risako.framework.core.bot.config

import dev.lunarcoffee.risako.framework.core.*
import dev.lunarcoffee.risako.framework.core.annotations.*
import dev.lunarcoffee.risako.framework.core.bot.*
import dev.lunarcoffee.risako.framework.core.commands.*
import dev.lunarcoffee.risako.framework.core.dispatchers.*
import dev.lunarcoffee.risako.framework.core.dispatchers.parsers.*
import mu.*
import net.dv8tion.jda.api.*
import net.dv8tion.jda.api.hooks.*
import org.yaml.snakeyaml.*
import java.io.*

internal class DefaultBot(configPath: String) : Bot {
    override lateinit var jda: JDA
    override lateinit var dispatcher: Dispatcher

    override val config = Yaml().loadAs(File(configPath).readText(), BotConfig::class.java)!!

    private val cl = ClassLoader.getSystemClassLoader()

    // Classes that contain commands. Would've done this with a nice library (like Reflections),
    // but the Kotlin compiler plugin I use for the code execution commands conflicts with
    // Reflections (which worked), so I had to do this.
    private val commandGroups = File(config.sourceRootDir)
        .walk()
        .mapNotNull {
            // This allows loading command groups in deeper package hierarchies.
            val classPath = it
                .absolutePath
                .replace("/", ".")
                .substringAfter(config.commandP)
                .substringBeforeLast(".")
            silence { cl.loadClass("${config.commandP}$classPath") }
        }
        .filter { c -> c.annotations.any { it.annotationClass == CommandGroup::class } }

    // Map of [CommandGroup]s to their [Command]s with messy reflection stuff.
    private val groupToCommands = commandGroups
        .map { group -> group.methods.filter { it.returnType == Command::class.java } }
        .zip(commandGroups.map { it.newInstance() })
        .associate { (methods, group) ->
            val annotation = group::class.annotations.find { it is CommandGroup } as CommandGroup
            annotation to methods.map {
                (it.invoke(group) as Command).apply { groupName = annotation.name }
            }
        }
        .also {
            log.info {
                val groupNames = it.keys.map { it.name }
                "Loaded command groups: $groupNames"
            }
        }

    // Mutable to allow for dynamic loading of commands.
    override val commands = groupToCommands.values.flatten().toMutableList()
    override val commandNames get() = commands.map { it.name }.sorted()

    // Register all classes marked with the [ListenerGroup] annotation as event listeners. Most, if
    // not all of this complexity, is checking for the correct types and constructor signatures
    // to prevent mistakes. And very painful reflection.
    private val listenerGroups = File(config.sourceRootDir)
        .walk()
        .mapNotNull {
            // This allows loading listener groups in deeper package hierarchies.
            val classPath = it
                .absolutePath
                .replace("/", ".")
                .substringAfter(config.listenerP)
                .substringBeforeLast(".")
            silence { cl.loadClass("${config.listenerP}$classPath") }
        }
        .filter { c -> c.annotations.any { it.annotationClass == ListenerGroup::class } }
        .map { c ->
            c.constructors.find {
                // Make sure the constructor takes one argument of type [Bot].
                it.parameters.run { size == 1 && get(0).type.name == Bot::class.java.name }
            }!!.newInstance(this) as ListenerAdapter
        }

    // Mutable to allow for dynamic loading of event listeners.
    override val listeners = listenerGroups.toMutableList()
    override val listenerNames
        get() = listeners
            .map { it.javaClass.name.substringAfterLast(".") }
            .sorted()

    init {
        jda = JDABuilder().setToken(config.token).build()
        dispatcher = GuildDispatcher(this, DefaultArgParser())

        jda.addEventListener(*listeners.toTypedArray())
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
    }
}
