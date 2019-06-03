package dev.lunarcoffee.risako.bot.exts.commands.mod.unmute

import dev.lunarcoffee.risako.bot.consts.*
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.extensions.*
import dev.lunarcoffee.risako.framework.core.scheduleNoInline
import dev.lunarcoffee.risako.framework.core.services.reloaders.*
import net.dv8tion.jda.api.events.GenericEvent
import java.util.*

@ReloadFrom(ColName.MUTE)
internal class UnmuteReloader(
    time: Date,
    val userId: String,
    val prevRoleIds: List<String>,
    val reason: String,
    val guildId: String,
    private val channelId: String,
    private val mutedRole: String
) : Reloadable(time) {

    init {
        colName = ColName.MUTE
    }

    override suspend fun schedule(event: GenericEvent, col: ReloadableCollection<Reloadable>) {
        DEFAULT_TIMER.scheduleNoInline(time) {
            // Stop if the mute is no longer in the database (it has been removed manually by an
            // <..unmute> command or similar).
            val muteStillActive = col.contains { it.rjid == rjid }
            if (!muteStillActive) {
                return@scheduleNoInline
            }

            val user = event.jda.getUserById(userId)!!
            val guild = event.jda.getGuildById(guildId)!!

            try {
                val prevRoles = prevRoleIds.mapNotNull { guild.getRoleById(it) }

                // Remove muted role and re-add original roles.
                guild
                    .controller
                    .modifyMemberRoles(
                        guild.getMember(user)!!,
                        prevRoles,
                        listOf(guild.getRoleById(mutedRole))
                    )
                    .await()

                val channel = guild.getTextChannelById(channelId)!!
                val pm = event.jda.getUserById(userId)!!.openPrivateChannel().await()

                channel.sendSuccess("`${user.asTag}` has been unmuted!")
                pm.send(
                    embed {
                        title = "${Emoji.HAMMER_AND_WRENCH}  You were automatically unmuted!"
                        description = """
                            |**Server name**: ${guild.name}
                            |**Roles regained**: ${prevRoles.map { it.asMention }}
                        """.trimMargin()
                    }
                )
            } finally {
                finish()
            }
        }
    }
}
