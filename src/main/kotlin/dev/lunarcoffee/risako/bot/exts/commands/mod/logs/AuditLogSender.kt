package dev.lunarcoffee.risako.bot.exts.commands.mod.logs

import dev.lunarcoffee.risako.bot.constToEng
import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.bot.consts.TIME_FORMATTER
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.dsl.embedPaginator
import dev.lunarcoffee.risako.framework.api.extensions.*
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.ContentSender
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.audit.TargetType
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException

internal class AuditLogSender(private val limit: Int) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        // Make sure the author can normally check audit logs.
        val guildAuthor = ctx.event.guild.getMember(ctx.event.author) ?: return
        if (!guildAuthor.hasPermission(Permission.VIEW_AUDIT_LOGS)) {
            ctx.sendError("You need to be able to view audit logs!")
            return
        }

        val logs = try {
            ctx.event.guild.retrieveAuditLogs().takeAsync(limit).await()
        } catch (e: InsufficientPermissionException) {
            ctx.sendError("I need to be able to view this server's audit logs!")
            return
        }

        val guildName = ctx.event.guild.name
        ctx.send(
            embedPaginator(ctx.event.author) {
                for (log in logs) {
                    page(
                        embed {
                            log.run {
                                // Name of audit target based on its type.
                                val name = runBlocking {
                                    getAuditTargetName(ctx, targetType, targetId)
                                }

                                title = "${Emoji.OPEN_BOOK}  Audit logs of **$guildName**:"
                                description = """
                                    |**Audit ID**: $id
                                    |**Type**: ${type.constToEng()}
                                    |**Initiator**: ${user?.asTag ?: "(none)"}
                                    |**Target type**: ${targetType.name.toLowerCase()}
                                    |**Target**: $name
                                    |**Time occurred**: ${timeCreated.format(TIME_FORMATTER)}
                                    |**Reason**: ${reason ?: "(no reason)"}
                                """.trimMargin()
                            }
                        }
                    )
                }
            }
        )
    }

    private suspend fun getAuditTargetName(
        ctx: CommandContext,
        type: TargetType,
        id: String
    ): String {

        return when (type) {
            TargetType.GUILD -> ctx.jda.getGuildById(id)!!.name
            TargetType.CHANNEL -> {
                val textChannel = ctx.event.guild.getTextChannelById(id)
                val voiceChannel = ctx.event.guild.getVoiceChannelById(id)

                val name = textChannel?.asMention ?: voiceChannel?.name?.run { "VC `$this`" }
                name ?: "(unavailable)"
            }
            TargetType.ROLE -> ctx.jda.getRoleById(id)?.asMention ?: "(unavailable)"
            TargetType.MEMBER -> ctx.event.guild.getMemberById(id)?.user?.asTag ?: "(unavailable)"
            TargetType.INVITE -> "(some invite link)"
            TargetType.WEBHOOK -> ctx.jda.retrieveWebhookById(id).await().name
            TargetType.EMOTE -> ctx.jda.getEmoteById(id)?.asMention ?: "(unavailable)"
            else -> "(unavailable)"
        }
    }
}