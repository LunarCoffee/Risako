package dev.lunarcoffee.risakobot.bot.exts.commands.mod.ban

import dev.lunarcoffee.risakobot.bot.exts.commands.mod.unban.UnbanInfoSender
import dev.lunarcoffee.risakobot.framework.api.extensions.*
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.exceptions.PermissionException

internal class BanController(private val ctx: CommandContext) {
    suspend fun ban(user: User, reason: String) {
        val guild = ctx.event.guild
        if (!checkPermissions(guild)) {
            return
        }

        val offender = guild.getMember(user)
        if (offender == null) {
            ctx.sendError("That user is not a member of this server!")
            return
        }

        // Make role interaction checks.
        if (!guild.selfMember.canInteract(offender)) {
            ctx.sendError("I don't have enough permissions to do that!")
            return
        }

        try {
            ctx.send(BanInfoSender(user, reason))
            guild.ban(offender, 0, reason).await()
        } catch (e: PermissionException) {
            ctx.sendError("I don't have enough permissions to do that!")
        }
    }

    suspend fun unban(user: User) {
        val guild = ctx.event.guild
        if (!checkPermissions(guild)) {
            return
        }

        try {
            ctx.send(UnbanInfoSender(user))
            guild.unban(user).await()
        } catch (e: PermissionException) {
            ctx.sendError("I don't have enough permissions to do that!")
        }
    }

    // Make sure the author can ban members.
    private suspend fun checkPermissions(guild: Guild): Boolean {
        val guildAuthor = guild.getMember(ctx.event.author) ?: return false
        if (!guildAuthor.hasPermission(Permission.BAN_MEMBERS)) {
            ctx.sendError("You need to be able to ban members!")
            return false
        }
        return true
    }
}
