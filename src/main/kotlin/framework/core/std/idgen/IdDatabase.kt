package framework.core.std.idgen

import framework.core.DB
import org.litote.kmongo.eq

internal object IdDatabase {
    private val activeCol = DB.getCollection<GeneratedId>("ActiveIdGen0")

    suspend fun register(id: Long) = activeCol.insertOne(GeneratedId(id))
    suspend fun delete(id: Long) = activeCol.deleteOne(GeneratedId::id eq id)
    suspend fun contains(id: Long) = activeCol.findOne(GeneratedId::id eq id) != null
}
