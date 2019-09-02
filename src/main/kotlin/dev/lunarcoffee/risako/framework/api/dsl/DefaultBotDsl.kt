package dev.lunarcoffee.risako.framework.api.dsl

import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.bot.DefaultBot
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity

class DefaultBotDsl(configPath: String) : DefaultBot(configPath) {
    var activity: Activity?
        get() = jda.presence.activity
        set(value) {
            jda.presence.activity = value
        }

    var status: OnlineStatus
        get() = jda.presence.status
        set(value) = jda.presence.setStatus(value)
}

inline fun startBot(configPath: String, crossinline init: DefaultBotDsl.() -> Unit): Bot {
    return DefaultBotDsl(configPath).apply {
        init()
        loadAllCommands()
    }
}
