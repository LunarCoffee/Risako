package framework.core.commands.transformers

import framework.core.commands.CommandContext
import framework.core.std.*
import net.dv8tion.jda.api.entities.User

internal class TrUser(
    override val optional: Boolean = false,
    override var default: User? = null
) : Transformer<User?> {

    override suspend fun transform(
        ctx: CommandContext,
        args: MutableList<String>
    ): OpResult<User?> {

        if (args.isEmpty()) {
            return if (optional) OpSuccess(default) else OpError()
        }

        val input = args.removeAt(0)
        val mentionMatch = USER_MENTION.matchEntire(input)

        return OpSuccess(
            when {
                input.length == 18 -> ctx.jda.getUserById(input)
                input.matches(USER_TAG) -> ctx.jda.getUserByTag(input)
                mentionMatch != null -> ctx.jda.getUserById(mentionMatch.groupValues[1])
                else -> ctx.jda.getUsersByName(input, true).firstOrNull()
            } ?: UserNotFound
        )
    }

    companion object {
        private val USER_TAG = """.+#\d{4}$""".toRegex()
        private val USER_MENTION = """<@!?(\d{18})>""".toRegex()
    }
}