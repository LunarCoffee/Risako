package dev.lunarcoffee.risako.framework.core.services.reloaders

import dev.lunarcoffee.risako.framework.core.DB
import dev.lunarcoffee.risako.framework.core.silence
import dev.lunarcoffee.risako.framework.core.std.idgen.IdGenerator
import kotlinx.coroutines.*
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.litote.kmongo.eq
import java.io.File

internal class ReloaderListener(
    private val sourceRootDir: String
) : ListenerAdapter(), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val cl = ClassLoader.getSystemClassLoader()!!

    override fun onReady(event: ReadyEvent) {
        for (c in loadReloadableClasses()) {
            val colName = reloadAnnotation(c).colName
            val col = DB.getCollection<Reloadable>(colName)

            launch {
                for (reloadable in col.find().toList()) {
                    if (reloadable.finished) {
                        // Remove the reloadable from the collection and free its ID for other use.
                        col.deleteOne(Reloadable::id eq reloadable.id)
                        IdGenerator.delete(reloadable.id)
                    } else {
                        reschedule(c, reloadable, event)
                    }
                }
            }
        }
    }

    private fun loadReloadableClasses(): Sequence<Class<*>> {
        return File(sourceRootDir)
            .walk()
            .mapNotNull {
                // This allows loading listener groups in deeper package hierarchies.
                val classPath = it
                    .absolutePath
                    .replace("/", ".")
                    .substringAfter("src.main.kotlin.")
                    .substringBeforeLast(".")
                silence { cl.loadClass(classPath) }
            }
            .filter { c ->
                val isReloadable = c.superclass == Reloadable::class.java
                isReloadable && c.annotations.any { it.annotationClass == ReloadFrom::class }
            }
    }

    private suspend fun reschedule(c: Class<*>, reloadable: Reloadable, event: GenericEvent) {
        val constructor = c.asSubclass(Reloadable::class.java).kotlin.constructors.first()

        // The first argument of the constructor should always be a [Date] object, with the rest
        // being optional (or a secondary constructor with only the first argument).
        constructor.callBy(mapOf(constructor.parameters[0] to reloadable.time))
            .apply {
                data = reloadable.data
                id = reloadable.id
                finished = reloadable.finished
            }
            .schedule(event)
    }

    private fun reloadAnnotation(c: Class<*>): ReloadFrom {
        return c.annotations.find { it.annotationClass == ReloadFrom::class }!! as ReloadFrom
    }
}
