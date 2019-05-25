package dev.lunarcoffee.risako.framework.core.bot.loaders

import dev.lunarcoffee.risako.framework.core.annotations.*
import dev.lunarcoffee.risako.framework.core.bot.*
import net.dv8tion.jda.api.hooks.*

internal class ListenerLoader(override val bot: Bot) : ComponentClassLoader() {
    // Gets all [ListenerAdapter] classes and makes sure that does various validity checks.
    val listeners = loadClasses(bot.config.listenerP)
        .filter { c -> c.annotations.any { it.annotationClass == ListenerGroup::class } }
        .map { c ->
            c.constructors.find {
                // Make sure the constructor takes one argument of type [Bot].
                it.parameters.run { size == 1 && get(0).type.name == Bot::class.java.name }
            }!!.newInstance(this) as ListenerAdapter
        }
        .toMutableList()
}
