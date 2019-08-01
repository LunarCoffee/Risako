package dev.lunarcoffee.risakobot.framework.core.bot.loaders

import dev.lunarcoffee.risakobot.framework.core.bot.Bot
import dev.lunarcoffee.risakobot.framework.core.silence
import dev.lunarcoffee.risakobot.framework.core.std.HasBot
import java.io.File

internal abstract class ComponentClassLoader : HasBot {
    private val cl = ClassLoader.getSystemClassLoader()!!

    protected fun loadClasses(packagePath: String): Sequence<Class<*>> {
        return File(bot.config.sourceRootDir).walk().mapNotNull {
            // This allows loading listener groups in deeper package hierarchies.
            val classPath = it
                .absolutePath
                .replace("/", ".")
                .substringAfter(packagePath)
                .substringBeforeLast(".")
            silence { cl.loadClass("$packagePath$classPath") }
        }
    }

    // Find a constructor of a class which takes one argument of type [Bot], call it, and return
    // the created instance.
    protected fun callConstructorWithBot(c: Class<*>): Any? {
        return c.constructors.find {
            it.parameters.run { size == 1 && get(0).type.name == Bot::class.java.name }
        }?.newInstance(bot)
    }
}
