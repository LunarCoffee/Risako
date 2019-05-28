package dev.lunarcoffee.risako.framework.core.services.reloaders

import dev.lunarcoffee.risako.framework.core.std.idgen.IdGenerator
import kotlinx.coroutines.runBlocking

class ReloadableJson(val json: String) {
    val id = runBlocking { IdGenerator.generate() }
}
