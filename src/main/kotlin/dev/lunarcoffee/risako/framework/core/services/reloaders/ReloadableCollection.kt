package dev.lunarcoffee.risako.framework.core.services.reloaders

import com.google.gson.Gson
import com.mongodb.client.result.DeleteResult
import dev.lunarcoffee.risako.framework.core.DB
import org.litote.kmongo.eq
import kotlin.reflect.KClass

class ReloadableCollection<T : Reloadable>(colName: String, private val c: KClass<out T>) {
    private val col = DB.getCollection<ReloadableJson>(colName)

    suspend fun find() = col.find().toList().mapToT()
    suspend fun find(filter: (T) -> Boolean) = find().filter(filter)
    suspend fun findOne(filter: (T) -> Boolean) = find(filter).firstOrNull()

    suspend fun contains(filter: (T) -> Boolean) = findOne(filter) != null
    suspend fun contains(element: T) = contains { it.rjid == element.rjid }

    suspend fun deleteOne(filter: (T) -> Boolean): DeleteResult {
        val idToDelete = find().find(filter)?.rjid ?: return DeleteResult.acknowledged(0)
        return col.deleteOne(ReloadableJson::id eq idToDelete)
    }

    private fun List<ReloadableJson>.mapToT() = map { GSON.fromJson(it.json, c.java) }

    companion object {
        private val GSON = Gson()
    }
}
