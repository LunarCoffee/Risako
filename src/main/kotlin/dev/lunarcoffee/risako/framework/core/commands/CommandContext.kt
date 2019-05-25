package dev.lunarcoffee.risako.framework.core.commands

import dev.lunarcoffee.risako.framework.core.dispatchers.*
import dev.lunarcoffee.risako.framework.core.std.*
import net.dv8tion.jda.api.entities.*

internal interface CommandContext : DispatchableContext, HasBot, MessageChannel
