package dev.lunarcoffee.risako.framework.api.extensions

import dev.lunarcoffee.risako.bot.consts.GSON
import dev.lunarcoffee.risako.framework.core.DB
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.services.reloaders.Reloadable
import dev.lunarcoffee.risako.framework.core.services.reloaders.ReloadableJson

// Schedules a [Reloadable] so that it can be rescheduled upon a bot restart.
internal suspend inline fun <reified T : Reloadable> CommandContext.scheduleReloadable(
    colName: String,
    vararg args: Any?
) {
    // Construct a new instance of the [Reloadable] represented by the provided type parameter,
    // which should be a user defined class extending [Reloadable].
    val reloadable = T::class.constructors.first().call(*args)
    val col = DB.getCollection<ReloadableJson>(colName)

    // [reloadableJson] is a JSON representation of the [Reloadable] object, used so the user
    // defined class can freely define properties while keeping the API friendly.
    val reloadableJson = ReloadableJson(GSON.toJson(reloadable))

    // Set the RJID of the reloadable to allow identification after being retrieved from the DB
    // after a restart. This allows calls to [Reloadable#finish] to function properly.
    reloadable.rjid = reloadableJson.id

    col.insertOne(reloadableJson)
    reloadable.schedule(event)
}
