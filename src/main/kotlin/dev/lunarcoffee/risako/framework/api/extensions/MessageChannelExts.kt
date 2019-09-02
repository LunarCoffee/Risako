package dev.lunarcoffee.risako.framework.api.extensions

import dev.lunarcoffee.risako.framework.core.services.paginators.Paginator
import dev.lunarcoffee.risako.framework.core.services.waiters.WaitList
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.exceptions.ErrorResponseException

suspend fun MessageChannel.send(msg: String, after: suspend (Message) -> Unit = {}): Message {
    return try {
        sendMessage(msg).await().apply { after(this) }
    } catch (e: ErrorResponseException) {
        sendError("The message that was supposed to be sent can't fit in a message!")
    }
}

suspend fun MessageChannel.send(
    embed: MessageEmbed,
    after: suspend (Message) -> Unit = {}
): Message {

    return try {
        sendMessage(embed).await().apply { after(this) }
    } catch (e: ErrorResponseException) {
        sendError("The message that was supposed to be sent can't fit in an embed!")
    }
}

suspend fun MessageChannel.send(message: Message, after: suspend (Message) -> Unit = {}): Message {
    return try {
        sendMessage(message).await().apply { after(this) }
    } catch (e: ErrorResponseException) {
        sendError("The message that was supposed to be sent can't fit in a message or embed!")
    }
}

suspend fun MessageChannel.send(paginator: Paginator, after: suspend (Paginator) -> Unit = {}) {
    try {
        after(paginator.apply { send(this@send) })
    } catch (e: ErrorResponseException) {
        sendError("The message that was supposed to be sent can't fit in an embed!")
    }
}

suspend fun MessageChannel.sendSuccess(
    msg: String,
    after: suspend (Message) -> Unit = {}
): Message {

    return send(":white_check_mark:  $msg  **\\o/**", after)
}

suspend fun MessageChannel.sendError(msg: String, after: suspend (Message) -> Unit = {}): Message {

    return send(":negative_squared_cross_mark:  $msg  **>.<**", after)
}

// Waits for a response from the [user] in the receiver channel, with a default timeout of 30
// seconds when it throws a [TimeoutException].
suspend fun MessageChannel.waitFor(user: User, timeout: Long = 30_000): Message {
    return WaitList.waitFor(user, this, timeout)
}
