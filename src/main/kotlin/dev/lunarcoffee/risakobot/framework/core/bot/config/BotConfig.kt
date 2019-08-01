package dev.lunarcoffee.risakobot.framework.core.bot.config

internal interface BotConfig {
    val prefix: String
    val token: String
    val ownerId: String

    val sourceRootDir: String
    val commandP: String
    val listenerP: String
}
