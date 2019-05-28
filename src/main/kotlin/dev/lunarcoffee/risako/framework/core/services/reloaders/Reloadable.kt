package dev.lunarcoffee.risako.framework.core.services.reloaders

import dev.lunarcoffee.risako.framework.core.DB
import dev.lunarcoffee.risako.framework.core.std.ReloadableMap
import dev.lunarcoffee.risako.framework.core.std.idgen.IdGenerator
import kotlinx.coroutines.*
import net.dv8tion.jda.api.events.GenericEvent
import org.litote.kmongo.eq
import java.util.*

internal open class Reloadable(
    val time: Date
) : CoroutineScope by CoroutineScope(Dispatchers.Default) {

    var data = ReloadableMap()

    var id = runBlocking { IdGenerator.generate() }
    var finished = false

    open suspend fun schedule(event: GenericEvent): Unit = throw IllegalArgumentException()

    suspend fun finish(colName: String) {
        val col = DB.getCollection<Reloadable>(colName)

        // Remove the reloadable from the collection and free its ID for other use.
        col.deleteOne(Reloadable::id eq id)
        IdGenerator.delete(id)
    }
}
