package dev.lunarcoffee.risakobot.framework.core.bot.config

internal class DefaultConfig : BotConfig {
    override lateinit var prefix: String
    override lateinit var token: String
    override lateinit var ownerId: String

    override lateinit var sourceRootDir: String
    override lateinit var commandP: String
    override lateinit var listenerP: String
}
