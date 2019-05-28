package dev.lunarcoffee.risako.framework.core.services.reloaders

import dev.lunarcoffee.risako.framework.core.DB
import dev.lunarcoffee.risako.framework.core.std.idgen.IdGenerator
import net.dv8tion.jda.api.events.GenericEvent
import org.litote.kmongo.eq
import java.util.*

internal open class Reloadable(val time: Date) {
    // ReloadableJsonID; a unique value representing this specific [Reloadable] and its DB form
    // (which is a [ReloadableJson] instance. This value is set externally by the extension
    // [CommandContext#scheduleReloadable] defined in <CommandContextExts.kt>, which also handles
    // adding the reloadable to the DB and scheduling it.
    var rjid = 0L

    lateinit var colName: String
    var finished = false

    open suspend fun schedule(event: GenericEvent): Unit = throw IllegalArgumentException()

    // Delete this [Reloadable] from the DB and free its ID (stored in [rjid]).
    suspend fun finish() {
        finished = true
        val col = DB.getCollection<ReloadableJson>(colName)

        col.deleteOne(ReloadableJson::id eq rjid)
        IdGenerator.delete(rjid)
    }
}
