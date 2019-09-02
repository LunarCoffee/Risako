package dev.lunarcoffee.risako.framework.core.bot.config

interface BotConfig {
    val prefix: String
    val token: String
    val ownerId: String

    val sourceRootDir: String
    val commandP: String
    val listenerP: String
}
