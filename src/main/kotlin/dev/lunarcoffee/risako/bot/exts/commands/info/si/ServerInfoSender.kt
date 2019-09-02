package dev.lunarcoffee.risako.bot.exts.commands.info.si

import dev.lunarcoffee.risako.bot.constToEng
import dev.lunarcoffee.risako.bot.consts.Emoji
import dev.lunarcoffee.risako.framework.api.dsl.embed
import dev.lunarcoffee.risako.framework.api.dsl.embedPaginator
import dev.lunarcoffee.risako.framework.api.extensions.send
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.ContentSender
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageEmbed

class ServerInfoSender(private val guild: Guild) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.send(
            ctx.embedPaginator {
                page(generalInfoEmbed())
                page(otherInfoEmbed(ctx))
            }
        )
    }

    private fun generalInfoEmbed(): MessageEmbed {
        return embed {
            guild.run {
                val afkChannel = afkChannel?.id?.run { "<#$this>" } ?: "(none)"
                val features = features.map { it.constToEng() }

                title = "${Emoji.MAG_GLASS}  Info on server **$name**:"
                this@embed.description = """
                    |**Guild ID**: $id
                    |**Total members**: ${members.size} members
                    |**Total emotes**: ${emotes.size} emotes
                    |**Total channels**: ${channels.size} channels
                    |**Text channels**: ${textChannels.size} text channels
                    |**Voice channels**: ${voiceChannels.size} voice channels
                    |**AFK channel**: $afkChannel
                    |**NSFW filter:** ${explicitContentLevel.description}
                    |**Special features**: ${features.ifEmpty { "(none)" }}
                """.trimMargin()

                thumbnail { url = iconUrl }
            }
        }
    }

    private fun otherInfoEmbed(ctx: CommandContext): MessageEmbed {
        return embed {
            guild.run {
                val roles = if (guild.id == ctx.event.guild.id)
                    roles.map { it.asMention }.toString()
                else
                    "(unavailable)"

                title = "${Emoji.MAG_GLASS}  Info on server **$name**:"
                this@embed.description = """
                    |**Owner**: ${owner?.user?.asTag ?: "(none)"}
                    |**Voice region**: ${region.getName()}
                    |**Roles**: ${roles.ifEmpty { "(none)" }}
                    |**Verification level**: ${verificationLevel.constToEng()}
                    |**MFA level**: ${requiredMFALevel.constToEng()}
                    |**Icon ID**: ${iconId ?: "(no icon)"}
                """.trimMargin()

                thumbnail { url = iconUrl }
            }
        }
    }
}
