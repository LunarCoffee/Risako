package dev.lunarcoffee.risako.framework.core.commands

import dev.lunarcoffee.risako.framework.core.dispatchers.Dispatchable
import dev.lunarcoffee.risako.framework.core.dispatchers.DispatchableArgs
import dev.lunarcoffee.risako.framework.core.std.HasBot

internal interface Command : Dispatchable<CommandContext, DispatchableArgs>, HasBot {
    val name: String
    var groupName: String
    val description: String
    val extDescription: String

    val aliases: Array<String>
    val names: Array<String>

    val ownerOnly: Boolean
    val nsfwOnly: Boolean
    val noArgParsing: Boolean

    val execute: suspend (CommandContext, DispatchableArgs) -> Unit
}
