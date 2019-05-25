package dev.lunarcoffee.risako.framework.core.bot.config

import dev.lunarcoffee.risako.framework.core.bot.BotConfig

internal class DefaultConfig : BotConfig {
    override lateinit var prefix: String
    override lateinit var token: String
    override lateinit var ownerId: String

    override lateinit var sourceRootDir: String
    override lateinit var commandP: String
    override lateinit var listenerP: String
}
