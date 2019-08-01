package dev.lunarcoffee.risakobot.framework.core.std

import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext

internal interface ContentSender {
    // This should send the content associated with the [ContentSender] to the channel [ctx.event].
    suspend fun send(ctx: CommandContext)
}
