package framework.core.std

import framework.core.commands.CommandContext

internal interface ContentSender {
    // This should send the content associated with the [ContentSender] to the channel [ctx.event].
    suspend fun send(ctx: CommandContext)
}
