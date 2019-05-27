package dev.lunarcoffee.risako.bot.exts.commands.utility.rpn

import dev.lunarcoffee.risako.framework.core.std.*
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

internal class RPNCalculator(private val expression: List<String>) {
    fun calculate(): OpResult<Double> {
        val stack = Stack<Double>()

        for (token in expression) {
            val number = token.toDoubleOrNull()
            if (number != null) {
                stack.push(number)
                continue
            }

            if (token !in operators) {
                return OpError()
            }

            val (op2, op1) = try {
                Pair(stack.pop(), stack.pop())
            } catch (e: EmptyStackException) {
                return OpError()
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

        return try {
            OpSuccess(stack.pop())
        } catch (e: EmptyStackException) {
            OpError()
        }
    }

    companion object {
        private val operators = arrayOf("+", "-", "*", "/", "**", "%", "&", "|", "^")
    }
}