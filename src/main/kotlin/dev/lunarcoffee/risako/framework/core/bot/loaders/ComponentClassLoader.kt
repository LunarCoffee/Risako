package dev.lunarcoffee.risako.framework.core.bot.loaders

import dev.lunarcoffee.risako.framework.core.*
import dev.lunarcoffee.risako.framework.core.std.*
import java.io.*

internal abstract class ComponentClassLoader : HasBot {
    private val cl = ClassLoader.getSystemClassLoader()!!

    fun loadClasses(packagePath: String): Sequence<Class<*>> {
        return File(bot.config.sourceRootDir)
            .walk()
            .mapNotNull {
                // This allows loading listener groups in deeper package hierarchies.
                val classPath = it
                    .absolutePath
                    .replace("/", ".")
                    .substringAfter(packagePath)
                    .substringBeforeLast(".")
                silence { cl.loadClass("$packagePath$classPath") }
            }
    }
}
