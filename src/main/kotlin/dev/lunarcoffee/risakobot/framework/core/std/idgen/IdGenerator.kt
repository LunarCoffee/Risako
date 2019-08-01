package dev.lunarcoffee.risakobot.framework.core.std.idgen

import kotlin.random.Random

internal object IdGenerator {
    suspend fun generate(): Long {
        var id: Long
        do {
            id = Random.nextLong(Long.MAX_VALUE)
        } while (IdDatabase.contains(id))

        IdDatabase.register(id)
        return id
    }

    suspend fun delete(id: Long) = IdDatabase.delete(id)
}
