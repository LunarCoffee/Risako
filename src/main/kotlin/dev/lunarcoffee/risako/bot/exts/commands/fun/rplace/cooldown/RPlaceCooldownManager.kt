package dev.lunarcoffee.risako.bot.exts.commands.`fun`.rplace.cooldown

import dev.lunarcoffee.risako.bot.consts.ColName
import dev.lunarcoffee.risako.framework.api.extensions.scheduleReloadable
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.services.reloaders.ReloadableCollection
import dev.lunarcoffee.risako.framework.core.std.SplitTime
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

internal object RPlaceCooldownManager {
    private val col = ReloadableCollection(ColName.RPLACE_COOLDOWN, RPlaceCooldownReloader::class)

    // Gets the time remaining until the user with ID [userId] is again able to put a pixel.
    // Returns null if the user is not on cooldown yet.
    suspend fun getRemainingCooldown(userId: String): SplitTime? {
        val time = col.findOne { it.userId == userId }?.time?.time ?: return null
        return SplitTime(time - System.currentTimeMillis())
    }

    suspend fun schedule(ctx: CommandContext, userId: String) {
        // Create a cooldown of 5 minutes.
        val inFiveMinutes = Date.from(
            LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant()
        )

        ctx.scheduleReloadable<RPlaceCooldownReloader>(
            ColName.RPLACE_COOLDOWN,
            inFiveMinutes,
            userId
        )
    }
}
