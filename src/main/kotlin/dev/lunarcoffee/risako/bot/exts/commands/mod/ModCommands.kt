package dev.lunarcoffee.risako.bot.exts.commands.mod

import dev.lunarcoffee.risako.bot.exts.commands.mod.mute.MuteController
import dev.lunarcoffee.risako.framework.api.dsl.command
import dev.lunarcoffee.risako.framework.core.annotations.CommandGroup
import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.commands.transformers.*
import dev.lunarcoffee.risako.framework.core.std.SplitTime
import net.dv8tion.jda.api.entities.User

@CommandGroup("Mod")
internal class ModCommands(private val bot: Bot) {
    fun mute() = command("mute") {
        description = "Mutes a member for a specified amount of time."
        aliases = arrayOf("silence", "softban")

        extDescription = """
            |`$name user time [reason]`\n
            |Mutes a user for a specified amount of time. I must have the permission to manage
            |roles. When a member is muted, they will be sent a message with `time` in a readable
            |format, the provided `reason` (or `(no reason)`) if none is provided, and the user
            |that muted them. You must be able to manage roles to use this command.
        """

        expectedArgs = arrayOf(TrUser(), TrTime(), TrRest(true, "(no reason)"))
        execute { args ->
            val user = args.get<User>(0)
            val time = args.get<SplitTime>(1)
            val reason = args.get<String>(2)
            MuteController(this).mute(user, time, reason)
        }
    }
}
