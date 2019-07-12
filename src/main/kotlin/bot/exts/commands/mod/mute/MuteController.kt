package bot.exts.commands.mod.mute

import bot.consts.ColName
import bot.exts.commands.mod.unmute.UnmuteInfoSender
import bot.exts.commands.mod.unmute.UnmuteReloader
import framework.api.extensions.*
import framework.core.commands.CommandContext
import framework.core.services.reloaders.ReloadableCollection
import framework.core.std.SplitTime
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.exceptions.PermissionException
import java.time.Instant
import java.util.*

internal class MuteController(private val ctx: CommandContext) {
    suspend fun mute(user: User, time: SplitTime, reason: String) {
        if (!checkInitiatorPermissions()) {
            return
        }

        if (col.contains { it.userId == user.id }) {
            ctx.sendError("That member is already muted!")
            return
        }

        val offender = getOffender(user) ?: return
        val mutedRole = getMutedRole() ?: return
        val oldRoles = offender.roles

        try {
            // Add muted role, remove original roles.
            ctx.event.guild.modifyMemberRoles(offender, listOf(mutedRole), oldRoles).await()
        } catch (e: IllegalArgumentException) {
            ctx.sendError("I can't remove managed roles!")
            return
        } catch (e: PermissionException) {
            ctx.sendError("I don't have enough permissions to do that!")
            return
        }

        ctx.send(MuteInfoSender(offender, time, reason))
        ctx.scheduleReloadable(
            ColName.MUTE,
            UnmuteReloader(
                Date.from(Instant.now().plusMillis(time.totalMs)),
                offender.id,
                oldRoles.map { it.id },
                reason,
                ctx.event.guild.id,
                ctx.event.channel.id,
                mutedRole.id
            )
        )
    }

    suspend fun unmute(user: User) {
        if (!checkInitiatorPermissions()) {
            return
        }
        val offender = getOffender(user) ?: return
        val mutedRole = getMutedRole() ?: return

        val reloadable = col.findOne { it.userId == user.id }
        if (reloadable == null) {
            ctx.sendError("That member isn't muted!")
            return
        }
        val originalRoles = ctx.event.guild.roles.filter { it.id in reloadable.prevRoleIds }

        try {
            // Remove muted role, add original roles.
            ctx.event.guild.modifyMemberRoles(offender, originalRoles, listOf(mutedRole)).await()
        } catch (e: PermissionException) {
            ctx.sendError("I don't have enough permissions to do that!")
            return
        }

        ctx.send(UnmuteInfoSender(offender))
        col.deleteOne { it.rjid == reloadable.rjid }
    }

    // Make sure the initiator or the mute/unmute can manage roles.
    private suspend fun checkInitiatorPermissions(): Boolean {
        val initiator = ctx.event.guild.getMember(ctx.event.author) ?: return false
        if (!initiator.hasPermission(Permission.MANAGE_ROLES)) {
            ctx.sendError("You need to be able to manage roles to mute users!")
            return false
        }
        return true
    }

    // Gets the member that is to be muted/unmuted.
    private suspend fun getOffender(user: User): Member? {
        val offender = ctx.event.guild.getMember(user)
        if (offender == null) {
            ctx.sendError("That user is not a member of this server!")
        }
        return offender
    }

    // Gets the role to give to members when they are muted.
    private suspend fun getMutedRole(): Role? {
        // Assume the guild has a role with "muted" in it, and get it.
        val mutedRole = ctx.event.guild.roles.find { it.name.contains("muted", true) }
        if (mutedRole == null) {
            ctx.sendError("I need to be able to assign a role with `muted` in its name!")
        }
        return mutedRole
    }

    companion object {
        private val col = ReloadableCollection(ColName.MUTE, UnmuteReloader::class)
    }
}
