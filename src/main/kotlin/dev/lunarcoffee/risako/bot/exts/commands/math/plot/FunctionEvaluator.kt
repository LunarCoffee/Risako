package dev.lunarcoffee.risako.bot.exts.commands.math.plot

import net.objecthunter.exp4j.ExpressionBuilder

internal class FunctionEvaluator(private val exprString: String) {
    // The loss of information during the conversion to [Double] isn't important, since the upper
    // bound of the displayed y-axis is much lower.
    fun evaluate(x: Double): Double? {
        return try {
            ExpressionBuilder(exprString)
                .variables("x")
                .build()
                .setVariable("x", x)
                .evaluate()
        } catch (e: ArithmeticException) {
            Double.POSITIVE_INFINITY
        } catch (e: Exception) {
            null
        }
    }
}
