package dev.lunarcoffee.risako.bot.exts.commands.mod.purge

import dev.lunarcoffee.risako.framework.api.extensions.sendError
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.exceptions.PermissionException

internal class ChannelPurger(private val ctx: CommandContext, private val limit: Int) {
    suspend fun purgeAll() {
        if (!checkParameters()) {
            return
        }

        try {
            ctx.purgeMessages(ctx.event.channel.iterableHistory.take(limit + 1))
        } catch (e: IllegalArgumentException) {
            ctx.sendError("I can't delete some messages because they're too old!")
        } catch (e: PermissionException) {
            ctx.sendError("I don't have enough permissions to do that!")
        }
    }

    suspend fun purgeFromUser(user: User, isAuthor: Boolean) {
        if (!checkParameters()) {
            return
        }

        try {
            // Purge messages from [user]. If [user] is the initiator, delete one more of their
            // messages than they specify, since the command invocation message shouldn't be
            // included.
            ctx.purgeMessages(
                ctx.event
                    .channel
                    .iterableHistory
                    .asSequence()
                    .filter { it.author == user }
                    .take(limit + if (isAuthor) 1 else 0)
                    .toList()
            )
        } catch (e: IllegalArgumentException) {
            ctx.sendError("I can't delete some messages because they're too old!")
        } catch (e: PermissionException) {
            ctx.sendError("I don't have enough permissions to do that!")
        }
    }

    private suspend fun checkParameters(): Boolean {
        // Make sure the author can manage messages.
        val guildAuthor = ctx.event.guild.getMember(ctx.event.author) ?: return false
        if (!guildAuthor.hasPermission(Permission.MESSAGE_MANAGE)) {
            ctx.sendError("You need to be able to manage messages!")
            return false
        }

        // Don't get rate limited!
        if (limit !in 1..100) {
            ctx.sendError("I can't purge that amount of messages!")
            return false
        }
        return true
    }
}
