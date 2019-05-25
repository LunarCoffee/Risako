package dev.lunarcoffee.risako.framework.core.commands

import dev.lunarcoffee.risako.framework.core.bot.Bot
import dev.lunarcoffee.risako.framework.core.dispatchers.DispatchableArgs

internal class GuildCommand(override val bot: Bot, override val name: String) :
    Command {
    override var groupName = "Misc"
    override val description = "(none)"
    override val extDescription = "(none)"

    override val aliases = emptyArray<String>()
    override val names get() = aliases + name

    override val ownerOnly = false
    override val nsfwOnly = false
    override val noArgParsing = false

    override lateinit var execute: suspend (CommandContext, DispatchableArgs) -> Unit

    override suspend fun dispatch(ctx: CommandContext, args: DispatchableArgs) {
        execute(ctx, args)
    }
}
