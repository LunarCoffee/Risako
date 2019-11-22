package dev.lunarcoffee.risako.bot

import dev.lunarcoffee.risako.framework.api.dsl.startBot
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity

fun main(args: Array<String>) {
    val path = args.getOrNull(0) ?: return System.err.println("No config file path passed!")
    startBot(path) {
        activity = Activity.watching("for ..help.")
        status = OnlineStatus.DO_NOT_DISTURB
    }
}
