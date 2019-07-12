package bot.exts.commands.math.rpn

import framework.api.extensions.sendError
import framework.api.extensions.sendSuccess
import framework.core.commands.CommandContext
import framework.core.std.ContentSender
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

internal class RPNCalculationSender(private val expression: List<String>) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        val stack = Stack<Double>()

        for (token in expression) {
            val number = token.toDoubleOrNull()
            if (number != null) {
                stack.push(number)
                continue
            }

            if (token !in operators) {
                ctx.sendError()
                return
            }

            val (op2, op1) = try {
                Pair(stack.pop(), stack.pop())
            } catch (e: EmptyStackException) {
                ctx.sendError()
                return
            }

            stack.push(
                when (token) {
                    "+" -> op1 + op2
                    "-" -> op1 - op2
                    "*" -> op1 * op2
                    "/" -> op1 / op2
                    "**" -> op1.pow(op2)
                    "%" -> op1 % op2
                    else -> {
                        val o1 = op1.roundToInt()
                        val o2 = op2.roundToInt()

                        when (token) {
                            "&" -> o1 and o2
                            "|" -> o1 or o2
                            "^" -> o1 xor o2
                            else -> throw IllegalStateException()
                        }.toDouble()
                    }
                }
            )
        }

        try {
            ctx.sendSuccess("The result of the calculation is `${stack.pop()}`!")
        } catch (e: EmptyStackException) {
            ctx.sendError()
        }
    }

    private suspend fun CommandContext.sendError() {
        sendError("Something was wrong with your expression!")
    }

    companion object {
        private val operators = arrayOf("+", "-", "*", "/", "**", "%", "&", "|", "^")
    }
}
