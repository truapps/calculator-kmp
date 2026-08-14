package com.truapps.calculator.app.main.engine

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class CalculatorEngine(
    val taxRate: Double = 18.0
) {

    private val _display: MutableState<String> =
        mutableStateOf("0")

    private val _expression: MutableState<String> =
        mutableStateOf("")

    private val _justCalculated: MutableState<Boolean> =
        mutableStateOf(false)

    private val _history: SnapshotStateList<String> =
        mutableStateListOf()

    val display: State<String> = _display

    val expression: State<String> = _expression

    val justCalculated: State<Boolean> = _justCalculated

    val history: List<String> = _history


    fun press(key: CalculatorKey) {

        when (key) {

            is CalculatorKey.Digit -> {
                inputDigit(key.value)
            }

            CalculatorKey.Decimal -> {
                inputDecimal()
            }

            CalculatorKey.Add -> {
                inputOperator("+")
            }

            CalculatorKey.Subtract -> {
                inputOperator("-")
            }

            CalculatorKey.Multiply -> {
                inputOperator("×")
            }

            CalculatorKey.Divide -> {
                inputOperator("÷")
            }

            CalculatorKey.SqRoot -> {
                inputSquareRoot()
            }

            CalculatorKey.Square -> {
                inputPower(2)
            }

            CalculatorKey.Cube -> {
                inputPower(3)
            }

            CalculatorKey.OpenParenthesis -> {
                openParenthesis()
            }

            CalculatorKey.CloseParenthesis -> {
                closeParenthesis()
            }

            CalculatorKey.PlusMinus -> {
                toggleSign()
            }

            CalculatorKey.Percentage -> {
                percentage()
            }

            CalculatorKey.TaxPlus -> {
                taxPlus()
            }

            CalculatorKey.TaxMinus -> {
                taxMinus()
            }

            CalculatorKey.Backspace -> {
                backspace()
            }

            CalculatorKey.Clear -> {
                clear()
            }

            CalculatorKey.Equals -> {
                calculate()
            }

            CalculatorKey.Left -> {
                // Cursor functionality can be added later.
            }

            CalculatorKey.Right -> {
                // Cursor functionality can be added later.
            }
        }
    }


    private fun inputDigit(digit: Int) {

        require(digit in 0..9)

        startNewExpressionIfNeeded()

        val expression = _expression.value

        /*
         * If the expression currently ends with:
         *
         * √(
         *
         * we simply append the number.
         */

        if (expression.isEmpty()) {
            _expression.value = digit.toString()
        } else {
            _expression.value += digit
        }

        updateDisplay()
    }


    private fun inputDecimal() {

        startNewExpressionIfNeeded()

        val currentNumber = getCurrentNumber()

        if (currentNumber.contains(".")) {
            return
        }

        if (
            _expression.value.isEmpty() ||
            _expression.value.last().isOperator() ||
            _expression.value.last() == '('
        ) {
            _expression.value += "0."
        } else {
            _expression.value += "."
        }

        updateDisplay()
    }


    private fun inputOperator(operator: String) {

        if (_justCalculated.value) {
            _justCalculated.value = false
        }

        if (_expression.value.isEmpty()) {

            // Allow negative number at beginning.
            if (operator == "-") {
                _expression.value = "-"
                updateDisplay()
            }

            return
        }

        val last = _expression.value.last()

        if (last.isOperator()) {

            _expression.value =
                _expression.value.dropLast(1) + operator

        } else if (last == '(') {

            // Only '-' is allowed after '('.
            if (operator == "-") {
                _expression.value += operator
            }

        } else {

            _expression.value += operator
        }

        updateDisplay()
    }


    private fun inputSquareRoot() {

        startNewExpressionIfNeeded()

        /*
         * √ becomes:
         *
         * √(
         *
         * Example:
         *
         * 5 + √(25
         */

        if (canImplicitlyMultiply()) {
            _expression.value += "×"
        }

        _expression.value += "√"

        updateDisplay()
    }


    private fun inputPower(power: Int) {

        if (_expression.value.isEmpty()) {
            return
        }

        val last = _expression.value.last()

        /*
         * x² / x³
         *
         * 5²
         * 2³
         */

        if (
            last.isDigit() ||
            last == ')' ||
            last == '%'
        ) {
            _expression.value += "^$power"
        }

        updateDisplay()
    }


    private fun openParenthesis() {

        startNewExpressionIfNeeded()

        if (canImplicitlyMultiply()) {
            _expression.value += "×"
        }

        _expression.value += "("

        updateDisplay()
    }


    private fun closeParenthesis() {

        val expression = _expression.value

        val openCount = expression.count { it == '(' }
        val closeCount = expression.count { it == ')' }

        if (openCount <= closeCount) {
            return
        }

        val last = expression.lastOrNull()

        if (
            last == null ||
            last.isOperator() ||
            last == '('
        ) {
            return
        }

        _expression.value += ")"

        updateDisplay()
    }


    private fun toggleSign() {

        if (_expression.value.isEmpty()) {
            _expression.value = "-"
            updateDisplay()
            return
        }

        val number = getCurrentNumber()

        if (number.isEmpty()) {
            return
        }

        val startIndex =
            _expression.value.length - number.length

        if (startIndex > 0 &&
            _expression.value[startIndex - 1] == '-'
        ) {

            val beforeMinus =
                if (startIndex - 1 > 0) {
                    _expression.value[startIndex - 2]
                } else {
                    null
                }

            if (
                beforeMinus == null ||
                beforeMinus.isOperator() ||
                beforeMinus == '('
            ) {
                _expression.value =
                    _expression.value.removeRange(
                        startIndex - 1,
                        startIndex
                    )

                updateDisplay()
                return
            }
        }

        _expression.value =
            _expression.value.substring(
                0,
                startIndex
            ) +
                    "-" +
                    _expression.value.substring(startIndex)

        updateDisplay()
    }


    private fun percentage() {

        if (_expression.value.isEmpty()) {
            return
        }

        val number = getCurrentNumber()

        if (number.isEmpty()) {
            return
        }

        _expression.value += "%"

        updateDisplay()
    }


    private fun taxPlus() {

        if (_expression.value.isEmpty()) {
            return
        }

        val number = getCurrentNumber()

        if (number.isEmpty()) {
            return
        }

        /*
         * 100 TAX+
         *
         * becomes
         *
         * 100 TAX+
         */

        _expression.value += "TAX+"

        updateDisplay()
    }


    private fun taxMinus() {

        if (_expression.value.isEmpty()) {
            return
        }

        val number = getCurrentNumber()

        if (number.isEmpty()) {
            return
        }

        _expression.value += "TAX-"

        updateDisplay()
    }


    private fun backspace() {

        if (_justCalculated.value) {
            clear()
            return
        }

        if (_expression.value.isEmpty()) {
            return
        }

        /*
         * Remove √(
         */

        if (_expression.value.endsWith("√(")) {

            _expression.value =
                _expression.value.dropLast(2)

        } else if (_expression.value.endsWith("TAX+")) {

            _expression.value =
                _expression.value.dropLast(4)

        } else if (_expression.value.endsWith("TAX-")) {

            _expression.value =
                _expression.value.dropLast(4)

        } else {

            _expression.value =
                _expression.value.dropLast(1)
        }

        updateDisplay()
    }


    private fun clear() {

        _expression.value = ""
        _display.value = "0"
        _justCalculated.value = false

        // Remove this line if AC should NOT clear history.
        _history.clear()
    }


    private fun calculate() {

        if (_expression.value.isBlank()) {
            return
        }

        try {

            val originalExpression =
                _expression.value

            val result =
                ExpressionEvaluator.evaluate(
                    expression = originalExpression,
                    taxRate = taxRate
                )

            val formattedResult =
                formatNumber(result)

            _history.add(
                "$originalExpression=$formattedResult"
            )

            _display.value =
                formattedResult

            /*
             * Keep the result as the new expression.
             *
             * Example:
             *
             * 5 + 5 = 10
             *
             * Then pressing × gives:
             *
             * 10 ×
             */

            _expression.value =
                formattedResult

            _justCalculated.value = true

        } catch (_: Exception) {

            _display.value = "Error"
            _justCalculated.value = true
        }
    }


    private fun startNewExpressionIfNeeded() {

        if (_justCalculated.value) {

            _expression.value = ""
            _display.value = "0"

            _justCalculated.value = false
        }
    }


    private fun canImplicitlyMultiply(): Boolean {

        val last =
            _expression.value.lastOrNull()
                ?: return false

        return last.isDigit() ||
                last == ')' ||
                last == '%'
    }


    private fun getCurrentNumber(): String {

        if (_expression.value.isEmpty()) {
            return ""
        }

        return Regex("""[-+]?\d*\.?\d+$""")
            .find(_expression.value)
            ?.value
            ?: ""
    }


    private fun updateDisplay() {

        _display.value =
            if (_expression.value.isEmpty()) {
                "0"
            } else {
                _expression.value
            }
    }


    private fun formatNumber(value: Double): String {

        if (value.isNaN() || value.isInfinite()) {
            return "Error"
        }

        if (value == value.toLong().toDouble()) {
            return value.toLong().toString()
        }

        return value
            .toString()
            .trimEnd('0')
            .trimEnd('.')
    }


    private fun Char.isOperator(): Boolean {

        return this == '+' ||
                this == '-' ||
                this == '×' ||
                this == '÷'
    }
}