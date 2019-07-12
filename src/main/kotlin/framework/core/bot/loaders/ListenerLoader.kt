package framework.core.bot.loaders

import framework.core.annotations.ListenerGroup
import framework.core.bot.Bot
import net.dv8tion.jda.api.hooks.ListenerAdapter

internal class ListenerLoader(override val bot: Bot) : ComponentClassLoader() {
    // Gets all [ListenerAdapter] classes and makes sure that does various validity checks.
    val listeners = loadClasses(bot.config.listenerP)
        .filter { c -> c.annotations.any { it.annotationClass == ListenerGroup::class } }
        .map { callConstructorWithBot(it)!! as ListenerAdapter }
        .toMutableList()
}
