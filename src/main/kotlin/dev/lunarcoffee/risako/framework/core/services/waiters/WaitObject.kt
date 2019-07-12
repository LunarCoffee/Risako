package dev.lunarcoffee.risako.framework.core.services.waiters

import kotlinx.coroutines.CompletableDeferred
import net.dv8tion.jda.api.entities.Message

internal interface WaitObject {
    val userId: String
    val channelId: String
    val message: CompletableDeferred<Message>
}
