package framework.core.commands

import framework.core.commands.transformers.Transformer
import framework.core.dispatchers.Dispatchable
import framework.core.dispatchers.DispatchableArgs
import framework.core.std.HasBot

internal interface Command : Dispatchable<CommandContext, DispatchableArgs>, HasBot {
    var name: String
    var groupName: String
    var description: String
    var extDescription: String

    var aliases: Array<String>
    val names: Array<String>

    var expectedArgs: Array<Transformer<out Any?>>

    var ownerOnly: Boolean
    var nsfwOnly: Boolean
    var deleteSender: Boolean
    var noArgParsing: Boolean

    var execute: suspend CommandContext.(DispatchableArgs) -> Unit

    fun execute(func: suspend CommandContext.(DispatchableArgs) -> Unit)
}
