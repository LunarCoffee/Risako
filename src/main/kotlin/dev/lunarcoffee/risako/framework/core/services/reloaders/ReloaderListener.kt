package dev.lunarcoffee.risako.framework.core.services.reloaders

import com.google.gson.Gson
import dev.lunarcoffee.risako.framework.core.DB
import dev.lunarcoffee.risako.framework.core.silence
import kotlinx.coroutines.*
import mu.KotlinLogging
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.io.File

internal class ReloaderListener(
    private val sourceRootDir: String
) : ListenerAdapter(), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val cl = ClassLoader.getSystemClassLoader()!!

    override fun onReady(event: ReadyEvent) {
        for (c in loadReloadableClasses()) {
            val colName = reloadAnnotation(c).colName
            val col = DB.getCollection<ReloadableJson>(colName)

            launch {
                val reloadables = col.find().toList()
                for (reloadableJson in reloadables) {
                    // Get an instance of the original reloadable class and reassign its
                    // ReloadableJsonID so we can identify it for removal.
                    val reloadable = c
                        .cast(GSON.fromJson(reloadableJson.json, c))
                        .apply { rjid = reloadableJson.id }

                    // Reschedule the reloadable. Each reloadable is responsible for calling
                    // [Reloadable#finish] to clean itself up.
                    reloadable.schedule(event, ReloadableCollection(colName, c.kotlin))
                }
                log.info { "Reloaded ${reloadables.size} reloadables from `$colName`!" }
            }
        }
    }

    private fun loadReloadableClasses(): Sequence<Class<out Reloadable>> {
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
                isReloadable && c.annotations.any { it is ReloadFrom }
            }
            .map { it.asSubclass(Reloadable::class.java) }
    }

    private fun reloadAnnotation(c: Class<*>): ReloadFrom {
        return c.annotations.find { it is ReloadFrom }!! as ReloadFrom
    }

    companion object {
        private val log = KotlinLogging.logger {}
        private val GSON = Gson()
    }
}
