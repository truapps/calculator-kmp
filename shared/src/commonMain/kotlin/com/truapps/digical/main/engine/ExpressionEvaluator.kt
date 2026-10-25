
package com.truapps.digical.main.engine

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import kotlin.math.pow
import kotlin.math.sqrt

object ExpressionEvaluator {

    class EvaluationException(message: String) :
        IllegalArgumentException(message)

    private data class ParsedValue(
        val value: Double,
        val isPercentage: Boolean = false
    )

    private val _errorPosition : MutableState<Int?> = mutableStateOf(null)

    val errorPosition : State<Int?> = _errorPosition

    fun evaluate(expression: String): Double {
        _errorPosition.value = null
        if (expression.isBlank()) {
            throw EvaluationException("Enter an expression")
        }

        val normalized = expression
            .replace(Regex("""\s*@\s*TAX\+(\d+(?:\.\d+)?)%""")) {
                "TAX+${it.groupValues[1]}"
            }
            .replace(Regex("""\s*@\s*TAX-(\d+(?:\.\d+)?)%""")) {
                "TAX-${it.groupValues[1]}"
            }

        val parser = Parser(normalized)
        val result = parser.parseExpression()
        parser.skipSpaces()

        if (!parser.isAtEnd()) {
            _errorPosition.value= parser.position+1
            throw EvaluationException(
                "Unexpected '${parser.peek()}' at position ${parser.position + 1}"
            )
        }

        if (!result.value.isFinite()) {
            throw EvaluationException("Result is out of range")
        }

        return result.value
    }

    private class Parser(private val expression: String) {
        var position: Int = 0
            private set

        fun parseExpression(): ParsedValue {
            var result = parseTerm()

            while (true) {
                skipSpaces()

                when (peek()) {
                    '+' -> {
                        position++
                        val right = parseTerm()

                        result = ParsedValue(
                            result.value +
                                    if (right.isPercentage) {
                                        result.value * right.value
                                    } else {
                                        right.value
                                    }
                        )
                    }

                    '-' -> {
                        position++
                        val right = parseTerm()

                        result = ParsedValue(
                            result.value -
                                    if (right.isPercentage) {
                                        result.value * right.value
                                    } else {
                                        right.value
                                    }
                        )
                    }

                    else -> return result
                }

                ensureFinite(result.value)
            }
        }

        private fun parseTerm(): ParsedValue {
            var result = parseUnary()

            while (true) {
                skipSpaces()

                when (peek()) {
                    '*', 'x', '×' -> {
                        position++
                        result = ParsedValue(
                            result.value * parseUnary().value
                        )
                    }

                    '/' , '÷' -> {
                        position++
                        skipSpaces()
                        val divisor = parseUnary().value

                        if (divisor == 0.0) {
                            _errorPosition.value = position + 1
                            throw EvaluationException("Cannot divide by zero")
                        }

                        result = ParsedValue(result.value / divisor)
                    }

                    // Implicit multiplication:
                    // 2√4 = 2 × √4
                    // 2(3+4) = 2 × (3+4)
                    // 10%200 = 0.1 × 200
                    else -> {
                        val next = peek()
                        val implicitMultiplication =
                            next == '√' ||
                                    next == '(' ||
                                    (result.isPercentage &&
                                            next != null && next.isDigit())

                        if (!implicitMultiplication) return result

                        val left = result.value
                        val right = parseUnary().value
                        result = ParsedValue(left * right)
                    }
                }

                ensureFinite(result.value)
            }
        }

        private fun parseUnary(): ParsedValue {
            skipSpaces()

            return when (peek()) {
                '+' -> {
                    position++
                    parseUnary()
                }

                '-' -> {
                    position++
                    ParsedValue(-parseUnary().value)
                }

                '√' -> {
                    position++
                    val operand = parseUnary().value

                    if (operand < 0.0) {
                        throw EvaluationException(
                            "Cannot calculate the square root of a negative number"
                        )
                    }

                    ParsedValue(sqrt(operand))
                }

                else -> parsePostfix()
            }
        }

        private fun parsePostfix(): ParsedValue {
            var result = parsePrimary()

            while (true) {
                skipSpaces()

                when {
                    match("²") -> {
                        result = ParsedValue(result.value.pow(2.0))
                    }

                    match("³") -> {
                        result = ParsedValue(result.value.pow(3.0))
                    }

                    match("%") -> {
                        if (result.isPercentage) {
                            throw EvaluationException(
                                "Repeated percentage operator"
                            )
                        }

                        result = ParsedValue(
                            result.value / 100.0,
                            isPercentage = true
                        )
                    }

                    match("TAX+") -> {
                        val rate = parseNumber().value
                        result = ParsedValue(
                            result.value * (1.0 + rate / 100.0)
                        )
                    }

                    match("TAX-") -> {
                        val rate = parseNumber().value
                        result = ParsedValue(
                            result.value * (1.0 - rate / 100.0)
                        )
                    }

                    else -> return result
                }

                ensureFinite(result.value)
            }
        }

        private fun parsePrimary(): ParsedValue {
            skipSpaces()

            if (match("(")) {
                val result = parseExpression()
                skipSpaces()

                if (!match(")")) {
                    throw EvaluationException("Missing closing parenthesis ')'")
                }

                return ParsedValue(result.value)
            }

            if (peek() == ')') {
                throw EvaluationException("Unexpected closing parenthesis ')'")
            }

            return parseNumber()
        }

        private fun parseNumber(): ParsedValue {
            skipSpaces()
            val start = position
            var decimalFound = false

            while (!isAtEnd()) {
                when (val char = expression[position]) {
                    in '0'..'9' -> position++

                    '.' -> {
                        if (decimalFound) {
                            throw EvaluationException(
                                "Invalid number: multiple decimal points"
                            )
                        }
                        decimalFound = true
                        position++
                    }

                    else -> break
                }
            }

            if (start == position) {
                _errorPosition.value = position + 1
                throw EvaluationException(
                    "Expected a number at position ${position + 1}"
                )
            }

            val text = expression.substring(start, position)

            if (text == ".") {
                throw EvaluationException("Invalid number")
            }

            val number = text.toDoubleOrNull()
                ?: throw EvaluationException("Invalid number: $text")

            if (!number.isFinite()) {
                throw EvaluationException("Number is out of range")
            }

            return ParsedValue(number)
        }

        private fun ensureFinite(value: Double) {
            if (!value.isFinite()) {
                throw EvaluationException("Result is out of range")
            }
        }

        fun skipSpaces() {
            while (!isAtEnd() && expression[position].isWhitespace()) {
                position++
            }
        }

        fun isAtEnd(): Boolean = position >= expression.length

        fun peek(): Char? =
            if (isAtEnd()) null else expression[position]

        private fun match(value: String): Boolean {
            if (!expression.regionMatches(position, value, 0, value.length)) {
                return false
            }

            position += value.length
            return true
        }
    }
}
