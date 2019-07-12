package framework.core.commands

import framework.core.bot.Bot
import framework.core.commands.transformers.Transformer
import framework.core.dispatchers.DispatchableArgs
import framework.core.trimToDescription

internal class GuildCommand(override val bot: Bot, override var name: String) : Command {
    override var groupName = "Misc"
    override var description = "(none)"
    override var extDescription = "(none)"
        set(value) {
            field = value.trimToDescription()
        }

    override var aliases = emptyArray<String>()
    override val names get() = aliases + name

    override var expectedArgs = emptyArray<Transformer<out Any?>>()

    override var ownerOnly = false
    override var nsfwOnly = false
    override var deleteSender = false
    override var noArgParsing = false

    override lateinit var execute: suspend CommandContext.(DispatchableArgs) -> Unit

    override fun execute(func: suspend CommandContext.(DispatchableArgs) -> Unit) {
        execute = func
    }

    override suspend fun dispatch(ctx: CommandContext, args: DispatchableArgs) = ctx.execute(args)
}
