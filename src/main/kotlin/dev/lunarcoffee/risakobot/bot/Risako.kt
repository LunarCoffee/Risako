package dev.lunarcoffee.risakobot.bot

import dev.lunarcoffee.risakobot.framework.api.dsl.startBot
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity

private fun main() {
    startBot("src/main/resources/config.yaml") {
        activity = Activity.watching("for ..help.")
        status = OnlineStatus.DO_NOT_DISTURB
    }
}
