package dev.lunarcoffee.risako.framework.core.commands

import dev.lunarcoffee.risako.framework.core.dispatchers.DispatchableContext
import dev.lunarcoffee.risako.framework.core.std.HasBot
import net.dv8tion.jda.api.entities.MessageChannel

internal interface CommandContext : DispatchableContext, HasBot, MessageChannel
