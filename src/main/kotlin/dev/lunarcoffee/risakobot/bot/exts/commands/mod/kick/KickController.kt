package dev.lunarcoffee.risakobot.bot.exts.commands.mod.kick

import dev.lunarcoffee.risakobot.framework.api.extensions.*
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.exceptions.PermissionException

internal class KickController(private val ctx: CommandContext) {
    suspend fun kick(user: User, reason: String) {
        val guild = ctx.event.guild

        // Make sure the author can kick members.
        val guildAuthor = guild.getMember(ctx.event.author) ?: return
        if (!guildAuthor.hasPermission(Permission.KICK_MEMBERS)) {
            ctx.sendError("You need to be able to kick members!")
            return
        }

        val offender = guild.getMember(user)
        if (offender == null) {
            ctx.sendError("That user is not a member of this server!")
            return
        }

        if (!guild.selfMember.canInteract(offender)) {
            ctx.sendError("I don't have enough permissions to do that!")
            return
        }

        try {
            ctx.send(KickInfoSender(user, reason))
            guild.kick(offender, reason).await()
        } catch (e: PermissionException) {
            ctx.sendError("I don't have enough permissions to do that!")
        }
    }
}
