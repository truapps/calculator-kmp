package com.truapps.calculator.app.main.engine

import kotlin.math.pow
import kotlin.math.sqrt

object ExpressionEvaluator {

    fun evaluate(expression: String): Double {

        val parser = Parser(expression)

        val result = parser.parseExpression()

        parser.skipSpaces()

        if (!parser.isAtEnd()) {
            error("Unexpected character at ${parser.position}")
        }

        return result
    }

    private class Parser(
        private val expression: String
    ) {

        var position: Int = 0
            private set

        // -----------------------------------------------------
        // +
        // -
        // -----------------------------------------------------

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

                    else -> return value
                }
            }
        }

        // -----------------------------------------------------
        // ×
        // ÷
        // %
        // -----------------------------------------------------

        private fun parseTerm(): Double {

            var value = parseUnary()

            while (true) {

                skipSpaces()

                when (peek()) {

                    '×', '*' -> {
                        position++
                        value *= parseUnary()
                    }

                    '÷', '/' -> {

                        position++

                        val divisor =
                            parseUnary()

                        if (divisor == 0.0) {
                            error("Division by zero")
                        }

                        value /= divisor
                    }

                    '%' -> {

                        position++

                        /*
                         * 20 % 150
                         *
                         * = 20% of 150
                         *
                         * = 30
                         */
                        val amount =
                            parseUnary()

                        value =
                            value * amount / 100.0
                    }

                    else -> return value
                }
            }
        }

        // -----------------------------------------------------
        // UNARY
        // -----------------------------------------------------

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
                        error("Square root of negative number")
                    }

                    sqrt(value)
                }

                else -> parsePostfix()
            }
        }

        // -----------------------------------------------------
        // POSTFIX
        // -----------------------------------------------------

        private fun parsePostfix(): Double {

            var value = parsePrimary()

            while (true) {

                skipSpaces()

                when {

                    match("²") -> {

                        value =
                            value.pow(2.0)
                    }

                    match("³") -> {

                        value =
                            value.pow(3.0)
                    }

                    match("TAX+") -> {

                        val rate =
                            parseNumber()

                        value +=
                            value * rate / 100.0
                    }

                    match("TAX-") -> {

                        val rate =
                            parseNumber()

                        value -=
                            value * rate / 100.0
                    }

                    else -> return value
                }
            }
        }

        // -----------------------------------------------------
        // PRIMARY
        // -----------------------------------------------------

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

        // -----------------------------------------------------
        // NUMBER
        // -----------------------------------------------------

        private fun parseNumber(): Double {

            skipSpaces()

            val start =
                position

            var decimalFound = false

            while (!isAtEnd()) {

                val char =
                    expression[position]

                when {

                    char.isDigit() -> {
                        position++
                    }

                    char == '.' &&
                            !decimalFound -> {

                        decimalFound = true
                        position++
                    }

                    else -> break
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

        // -----------------------------------------------------
        // MATCH
        // -----------------------------------------------------

        private fun match(
            value: String
        ): Boolean {

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

        // -----------------------------------------------------
        // PEEK
        // -----------------------------------------------------

        private fun peek(): Char? {

            return if (isAtEnd()) {
                null
            } else {
                expression[position]
            }
        }

        // -----------------------------------------------------
        // SPACES
        // -----------------------------------------------------

        fun skipSpaces() {

            while (
                !isAtEnd() &&
                expression[position].isWhitespace()
            ) {
                position++
            }
        }

        // -----------------------------------------------------
        // END
        // -----------------------------------------------------

        fun isAtEnd(): Boolean {
            return position >= expression.length
        }
    }
}