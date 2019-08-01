package dev.lunarcoffee.risakobot.bot.exts.commands.service.osu.user

import dev.lunarcoffee.risakobot.bot.consts.Emoji
import dev.lunarcoffee.risakobot.bot.localWithoutWeekday
import dev.lunarcoffee.risakobot.framework.api.dsl.embed
import dev.lunarcoffee.risakobot.framework.api.extensions.send
import dev.lunarcoffee.risakobot.framework.api.extensions.sendError
import dev.lunarcoffee.risakobot.framework.core.commands.CommandContext
import dev.lunarcoffee.risakobot.framework.core.std.ContentSender

internal class OsuUserSender(
    private val usernameOrId: String,
    private val mode: Int
) : ContentSender {

    override suspend fun send(ctx: CommandContext) {
        val info = OsuUserRequester(usernameOrId, mode).get()
        if (info == null) {
            ctx.sendError("I can't find a player with that name!")
            return
        }

        ctx.send(
            embed {
                info.run {
                    title = "${modeEmoji()}  Info on player **$username**:"
                    description = """
                        |**User ID**: $userId
                        |**Global rank**: #$globalRank
                        |**Country rank**: #$countryRank in $country
                        |**Accuracy**: $accuracy%
                        |**PP**: $pp
                        |**SS+/SS/S+/S/A**: $ssh/$ss/$sh/$s/$a
                        |**Join time**: ${joinTime.localWithoutWeekday()}
                        |**Play time**: $playTime
                        |**Link**: [profile link](https://osu.ppy.sh/users/$userId/$modeUrl)
                    """.trimMargin()

                    thumbnail { url = "https://a.ppy.sh/$userId" }
                }
            }
        )
    }

    private fun modeEmoji(): String {
        return when (mode) {
            0 -> Emoji.COMPUTER_MOUSE
            1 -> Emoji.DRUM
            2 -> Emoji.PINEAPPLE
            3 -> Emoji.MUSICAL_KEYBOARD
            else -> throw IllegalStateException()
        }
    }
}
