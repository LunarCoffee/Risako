package dev.lunarcoffee.risako.framework.core.commands.impl

import dev.lunarcoffee.risako.framework.core.bot.*
import dev.lunarcoffee.risako.framework.core.commands.Command
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.dispatchers.*

internal class GuildCommand(override val bot: Bot, override val name: String) :
    Command {
    override val groupName = "Misc"
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
