package dev.lunarcoffee.risako.framework.core.std

import dev.lunarcoffee.risako.framework.core.commands.CommandContext

internal interface ContentSender {
    // This should send the content associated with the [ContentSender] to the channel [ctx.event].
    suspend fun send(ctx: CommandContext)
}
