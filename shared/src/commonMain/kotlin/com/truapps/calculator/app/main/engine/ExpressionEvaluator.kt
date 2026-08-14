package com.truapps.calculator.app.main.engine

import kotlin.math.pow
import kotlin.math.sqrt

internal object ExpressionEvaluator {

    fun evaluate(
        expression: String,
        taxRate: Double = 18.0
    ): Double {

        val parser = Parser(
            expression = expression,
            taxRate = taxRate
        )

        val result = parser.parseExpression()

        parser.skipSpaces()

        if (!parser.isAtEnd()) {
            error(
                "Unexpected character at ${parser.position}"
            )
        }

        return result
    }


    private class Parser(
        private val expression: String,
        private val taxRate: Double
    ) {

        var position: Int = 0
            private set


        /*
         * expression
         *
         *     5 + 2 × 3
         *
         * becomes:
         *
         *     5 + (2 × 3)
         */

        fun parseExpression(): Double {

            var value = parseTerm()

            while (true) {

                skipSpaces()

                when (peek()) {

                    '+' -> {
                        position++
                        value += parseTerm()
                    }

                    '-' -> {
                        position++
                        value -= parseTerm()
                    }

                    else -> {
                        return value
                    }
                }
            }
        }


        /*
         * term
         *
         * Handles:
         *
         * ×
         * ÷
         */

        private fun parseTerm(): Double {

            var value = parsePower()

            while (true) {

                skipSpaces()

                when (peek()) {

                    '×', '*' -> {
                        position++
                        value *= parsePower()
                    }

                    '÷', '/' -> {

                        position++

                        val divisor =
                            parsePower()

                        if (divisor == 0.0) {
                            error("Division by zero")
                        }

                        value /= divisor
                    }

                    else -> {
                        return value
                    }
                }
            }
        }


        /*
         * power
         *
         * Examples:
         *
         * 2^3
         * 5²
         * 2³
         */

        private fun parsePower(): Double {

            var value = parseUnary()

            skipSpaces()

            while (true) {

                when {

                    match("^") -> {

                        val exponent =
                            parseUnary()

                        value =
                            value.pow(exponent)
                    }

                    match("²") -> {
                        value = value.pow(2)
                    }

                    match("³") -> {
                        value = value.pow(3)
                    }

                    else -> {
                        break
                    }
                }

                skipSpaces()
            }

            return value
        }


        /*
         * unary
         *
         * Handles:
         *
         * -5
         * +5
         * √25
         */

        private fun parseUnary(): Double {

            skipSpaces()

            return when {

                match("+") -> {
                    parseUnary()
                }

                match("-") -> {
                    -parseUnary()
                }

                match("√") -> {

                    val value =
                        parseUnary()

                    if (value < 0) {
                        error(
                            "Square root of negative number"
                        )
                    }

                    sqrt(value)
                }

                else -> {
                    parsePostfix()
                }
            }
        }


        /*
         * postfix
         *
         * Handles:
         *
         * %
         * TAX+
         * TAX-
         */

        private fun parsePostfix(): Double {

            var value = parsePrimary()

            while (true) {

                skipSpaces()

                when {

                    match("%") -> {
                        value /= 100.0
                    }

                    match("TAX+") -> {

                        value +=
                            value * taxRate / 100.0
                    }

                    match("TAX-") -> {

                        value -=
                            value * taxRate / 100.0
                    }

                    else -> {
                        return value
                    }
                }
            }
        }


        /*
         * primary
         *
         * Number or parentheses
         */

        private fun parsePrimary(): Double {

            skipSpaces()

            if (match("(")) {

                val value =
                    parseExpression()

                skipSpaces()

                if (!match(")")) {
                    error("Missing ')'")
                }

                return value
            }

            return parseNumber()
        }


        private fun parseNumber(): Double {

            skipSpaces()

            val start =
                position

            var hasDecimal = false

            while (!isAtEnd()) {

                val char =
                    expression[position]

                when {

                    char.isDigit() -> {
                        position++
                    }

                    char == '.' && !hasDecimal -> {

                        hasDecimal = true
                        position++
                    }

                    else -> {
                        break
                    }
                }
            }

            if (start == position) {
                error(
                    "Expected number at $position"
                )
            }

            return expression
                .substring(start, position)
                .toDouble()
        }


        private fun match(value: String): Boolean {

            if (
                expression.regionMatches(
                    position,
                    value,
                    0,
                    value.length
                )
            ) {

                position += value.length
                return true
            }

            return false
        }


        fun skipSpaces() {

            while (
                !isAtEnd() &&
                expression[position].isWhitespace()
            ) {
                position++
            }
        }


        fun isAtEnd(): Boolean {

            return position >= expression.length
        }


        private fun peek(): Char? {

            return if (isAtEnd()) {
                null
            } else {
                expression[position]
            }
        }
    }
}