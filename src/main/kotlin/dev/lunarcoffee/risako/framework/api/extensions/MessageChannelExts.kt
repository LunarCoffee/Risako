package dev.lunarcoffee.risako.framework.api.extensions

import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.exceptions.ErrorResponseException

internal suspend fun MessageChannel.send(
    msg: String,
    after: suspend (Message) -> Unit = {}
): Message {

    return try {
        sendMessage(msg).await().apply { after(this) }
    } catch (e: ErrorResponseException) {
        error("The message that was supposed to be sent can't fit in a message!")
    }
}

internal suspend fun MessageChannel.send(
    embed: MessageEmbed,
    after: suspend (Message) -> Unit = {}
): Message {

    return try {
        sendMessage(embed).await().apply { after(this) }
    } catch (e: ErrorResponseException) {
        error("The message that was supposed to be sent can't fit in an embed!")
    }
}

internal suspend fun MessageChannel.send(
    message: Message,
    after: suspend (Message) -> Unit = {}
): Message {

    return try {
        sendMessage(message).await().apply { after(this) }
    } catch (e: ErrorResponseException) {
        error("The message that was supposed to be sent can't fit in a message or embed!")
    }
}

internal suspend fun CommandContext.success(
    msg: String,
    after: suspend (Message) -> Unit = {}
): Message {

    return send(":white_check_mark:  $msg  **\\o/**", after)
}

internal suspend fun MessageChannel.error(
    msg: String,
    after: suspend (Message) -> Unit = {}
): Message {

    return send(":negative_squared_cross_mark:  $msg  **>.<**", after)
}
