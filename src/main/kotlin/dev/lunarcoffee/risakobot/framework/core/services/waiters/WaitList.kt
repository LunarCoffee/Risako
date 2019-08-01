package dev.lunarcoffee.risakobot.framework.core.services.waiters

import kotlinx.coroutines.CompletableDeferred
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeoutException
import kotlin.concurrent.schedule

internal object WaitList : ListenerAdapter() {
    // User IDs and channel IDs in the format "<userid><channelid>" to their [DefaultWaitObject]s.
    private val toWait = ConcurrentHashMap<String, WaitObject>()
    private val timer = Timer(true)

    // This function throws an exception after milliseconds, or returns normally if a message is
    // received before that time has passed.
    suspend fun waitFor(user: User, channel: MessageChannel, timeoutMs: Long = 30_000): Message {
        val deferred = CompletableDeferred<Message>(null)
        val deferredId = user.id + channel.id

        timer.schedule(timeoutMs) {
            toWait[deferredId]?.message?.completeExceptionally(TimeoutException())
        }

        toWait[deferredId] = DefaultWaitObject(user.id, channel.id, deferred)
        return deferred.await()
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        waitFinished(event.author.id + event.channel.id, event.message)
    }

    private fun waitFinished(deferredId: String, message: Message) {
        toWait[deferredId]?.message?.complete(message)
        toWait.remove(deferredId)
    }
}
