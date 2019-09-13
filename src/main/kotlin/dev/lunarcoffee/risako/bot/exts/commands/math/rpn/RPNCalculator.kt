package dev.lunarcoffee.risako.bot.exts.commands.math.rpn

import dev.lunarcoffee.risako.framework.api.extensions.sendError
import dev.lunarcoffee.risako.framework.api.extensions.sendSuccess
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.std.ContentSender
import java.math.BigDecimal
import java.util.*

class RPNCalculationSender(private val expression: List<String>) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        val stack = Stack<BigDecimal>()

        for (token in expression) {
            val number = token.toBigDecimalOrNull()
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
                    "/" -> op1.divide(op2)
                    "**" -> op1.pow(op2.intValueExact())
                    "%" -> op1 % op2
                    else -> {
                        val o1 = op1.toBigIntegerExact()
                        val o2 = op2.toBigIntegerExact()

                        when (token) {
                            "&" -> o1 and o2
                            "|" -> o1 or o2
                            "^" -> o1 xor o2
                            else -> throw IllegalStateException()
                        }.toBigDecimal()
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
