package framework.core.services.paginators

import framework.api.dsl.message
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import java.util.*

internal open class MessagePaginator(
    override val creator: User,
    override val closeTimer: Timer
) : Paginator() {

    override lateinit var closeTask: TimerTask
    override lateinit var message: Message

    override val pages = mutableListOf<Message>()
    override var currentPage = 0

    override fun formatMessage(): Message {
        return message {
            content = if (totalPages == 1) {
                pages[currentPage].contentRaw
            } else {
                "[${currentPage + 1}/$totalPages]\n${pages[currentPage].contentRaw}"
            }
        }
    }
}
