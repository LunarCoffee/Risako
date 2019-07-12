package framework.api.dsl

import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed

internal class MessageDsl {
    var content = ""
    var embed: MessageEmbed? = null

    fun create() = MessageBuilder().setContent(content).setEmbed(embed).build()
}

internal inline fun message(crossinline init: MessageDsl.() -> Unit): Message {
    return MessageDsl().apply(init).create()
}
