package dev.lunarcoffee.risako.bot.exts.commands.math.plot

import dev.lunarcoffee.risako.framework.core.silence
import net.objecthunter.exp4j.ExpressionBuilder
import java.util.concurrent.Executors

internal class FunctionEvaluator(private val exprString: String) {
    fun evaluate(x: Double): Double? {
        return silence {
            ExpressionBuilder(exprString)
                .variables("x")
                .build()
                .setVariable("x", x)
                .evaluateAsync(executor)
                .get()
        }
    }

    companion object {
        private val executor = Executors.newFixedThreadPool(4)
    }
}
