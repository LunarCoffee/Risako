package dev.lunarcoffee.framework.core.paginators

import dev.lunarcoffee.risakobot.framework.api.dsl.message
import dev.lunarcoffee.risakobot.framework.core.services.paginators.Paginator
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import java.util.*

internal open class EmbedPaginator(
    override val creator: User,
    override val closeTimer: Timer
) : Paginator() {

    override lateinit var closeTask: TimerTask
    override lateinit var message: Message

    override val pages = mutableListOf<Message>()
    override var currentPage = 0

    override fun formatMessage(): Message {
        return message {
            if (totalPages == 1) {
                embed = pages[currentPage].embeds[0]
            } else {
                content = "[${currentPage + 1}/$totalPages]"
                embed = pages[currentPage].embeds[0]
            }
        }
    }
}
