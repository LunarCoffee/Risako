package dev.lunarcoffee.risako.framework.api.dsl

import dev.lunarcoffee.framework.core.paginators.EmbedPaginator
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.services.paginators.MessagePaginator
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User

internal class MessagePaginatorDsl(creator: User) : MessagePaginator(creator, timer) {
    fun page(content: String) {
        pages += message { this@message.content = content }
    }
}

internal class EmbedPaginatorDsl(creator: User) : EmbedPaginator(creator, timer) {
    fun page(embed: MessageEmbed) {
        pages += message { this@message.embed = embed }
    }
}

internal inline fun messagePaginator(
    creator: User,
    crossinline init: MessagePaginatorDsl.() -> Unit
): MessagePaginator {

    return MessagePaginatorDsl(creator).apply(init)
}

internal inline fun embedPaginator(
    creator: User,
    crossinline init: EmbedPaginatorDsl.() -> Unit
): EmbedPaginator {

    return EmbedPaginatorDsl(creator).apply(init)
}

// Convenience method that automatically makes the paginator owned by the author of the event.
internal inline fun CommandContext.messagePaginator(
    creator: User = event.author,
    crossinline init: MessagePaginatorDsl.() -> Unit
): MessagePaginator {

    return MessagePaginatorDsl(creator).apply(init)
}

// Convenience method that automatically makes the paginator owned by the author of the event.
internal inline fun CommandContext.embedPaginator(
    creator: User = event.author,
    crossinline init: EmbedPaginatorDsl.() -> Unit
): EmbedPaginator {

    return EmbedPaginatorDsl(creator).apply(init)
}
