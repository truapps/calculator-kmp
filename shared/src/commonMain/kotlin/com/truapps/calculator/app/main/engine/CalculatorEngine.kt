package com.truapps.calculator.app.main.engine

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlin.math.pow
import kotlin.math.sqrt

class CalculatorEngine {

    private val _display = mutableStateOf("0")
    val display: State<String> = _display

    /**
     * Internal expression used by ExpressionEvaluator.
     *
     * Example:
     * 100TAX+18
     */
    private val _expression = mutableStateOf("")

    /**
     * Pretty expression shown in UI.
     *
     * Example:
     * 100 @ TAX+18%
     */
    private val _displayExpression = mutableStateOf("")
    val expression: State<String> = _displayExpression

    private val _justCalculated = mutableStateOf(false)
    val justCalculated: State<Boolean> = _justCalculated

    private val _history = mutableStateListOf<String>()
    val history: List<String> = _history

    /**
     * True while entering the tax percentage.
     *
     * Example:
     *
     * 100 @ TAX+
     *             ↑
     *        enteringTaxRate
     */
    private var enteringTaxRate = false

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

            CalculatorKey.Percentage -> {
                percentage()
            }

            CalculatorKey.SqRoot -> {
                squareRoot()
            }

            CalculatorKey.Square -> {
                square()
            }

            CalculatorKey.Cube -> {
                cube()
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

            CalculatorKey.TaxPlus -> {
                startTax(plus = true)
            }

            CalculatorKey.TaxMinus -> {
                startTax(plus = false)
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

            CalculatorKey.Left -> Unit
            CalculatorKey.Right -> Unit
        }
    }

    // ---------------------------------------------------------
    // DIGIT
    // ---------------------------------------------------------

    private fun inputDigit(digit: Int) {

        if (_justCalculated.value) {
            clearCurrent()
            _justCalculated.value = false
        }

        if (enteringTaxRate) {

            /*
             * Internal:
             *
             * 100TAX+
             *
             * 1 -> 100TAX+1
             * 8 -> 100TAX+18
             */
            _expression.value += digit

            /*
             * Display:
             *
             * 100 @ TAX+
             *
             * 1 -> 100 @ TAX+1%
             * 8 -> 100 @ TAX+18%
             */
            if (_display.value == "0") {
                _display.value = digit.toString()
            } else {
                _display.value += digit
            }

            updateTaxDisplay()

            return
        }

        if (_display.value == "0") {
            _display.value = digit.toString()
        } else {
            _display.value += digit
        }

        syncDisplayToExpression()
    }

    // ---------------------------------------------------------
    // DECIMAL
    // ---------------------------------------------------------

    private fun inputDecimal() {

        if (_justCalculated.value) {
            clearCurrent()
            _justCalculated.value = false
        }

        if (_display.value.contains(".")) {
            return
        }

        if (enteringTaxRate) {

            /*
             * Internal:
             *
             * 100TAX+18.
             */
            _expression.value += "."

            _display.value += "."

            updateTaxDisplay()

            return
        }

        if (_display.value == "0") {
            _display.value = "0."
        } else {
            _display.value += "."
        }

        syncDisplayToExpression()
    }

    // ---------------------------------------------------------
    // NORMAL OPERATORS
    // ---------------------------------------------------------

    private fun inputOperator(operator: String) {

        /*
         * TAX rate must be completed first.
         */
        if (enteringTaxRate) {

            if (_display.value == "0") {
                return
            }

            enteringTaxRate = false
        }

        if (_expression.value.isEmpty()) {
            _expression.value = _display.value
            _displayExpression.value = _display.value
        }

        val last = _expression.value.lastOrNull()

        if (last?.isOperator() == true) {

            _expression.value =
                _expression.value.dropLast(1) + operator

            _displayExpression.value =
                _displayExpression.value.dropLast(1) + operator

            _display.value = "0"

            return
        }

        /*
         * Don't allow:
         *
         * 20%+
         *
         * without a second operand.
         */
        if (last == '%') {
            return
        }

        _expression.value += operator
        _displayExpression.value += operator

        _display.value = "0"
        _justCalculated.value = false
    }

    // ---------------------------------------------------------
    // PERCENTAGE
    // ---------------------------------------------------------

    /**
     * Binary percentage.
     *
     * 20 % 150
     *
     * = 30
     *
     * NOT:
     *
     * 20 / 100
     */
    private fun percentage() {

        if (enteringTaxRate) {
            return
        }

        /*
         * Important:
         *
         * 20 + 30 = 50
         *
         * then %
         *
         * should become:
         *
         * 50%
         *
         * and must NOT reset the expression.
         */
        if (_justCalculated.value) {
            _justCalculated.value = false
        }

        if (_expression.value.isEmpty()) {
            _expression.value = _display.value
            _displayExpression.value = _display.value
        }

        val last = _expression.value.lastOrNull()

        if (
            last == null ||
            last.isOperator() ||
            last == '%' ||
            last == '('
        ) {
            return
        }

        _expression.value += "%"
        _displayExpression.value += "%"

        /*
         * Next digits are the second operand.
         *
         * 20 % 150
         */
        _display.value = "0"
    }

    // ---------------------------------------------------------
    // TAX
    // ---------------------------------------------------------

    /**
     * TAX+
     *
     * User sees:
     *
     * 100 @ TAX+
     *
     * Then entering 18:
     *
     * 100 @ TAX+18%
     *
     * Internal:
     *
     * 100TAX+18
     */
    private fun startTax(plus: Boolean) {

        if (enteringTaxRate) {
            return
        }

        if (_expression.value.isEmpty()) {
            _expression.value = _display.value
            _displayExpression.value = _display.value
        }

        val last = _expression.value.lastOrNull()

        if (
            last == null ||
            last.isOperator() ||
            last == '%' ||
            last == '('
        ) {
            return
        }

        /*
         * Internal evaluator expression.
         */
        _expression.value += if (plus) {
            "TAX+"
        } else {
            "TAX-"
        }

        /*
         * User-facing expression.
         */
        _displayExpression.value += if (plus) {
            " @ TAX+"
        } else {
            " @ TAX-"
        }

        enteringTaxRate = true

        /*
         * Tax rate input starts from zero.
         */
        _display.value = "0"
    }

    /**
     * Updates:
     *
     * 100 @ TAX+
     *
     * into:
     *
     * 100 @ TAX+18%
     */
    private fun updateTaxDisplay() {

        /*
         * Remove the previous rate.
         */
        val base = _displayExpression.value
            .substringBeforeLast("TAX+")
            .takeIf {
                _displayExpression.value.contains("TAX+")
            }
            ?: _displayExpression.value
                .substringBeforeLast("TAX-")
                .takeIf {
                    _displayExpression.value.contains("TAX-")
                }

        val isPlus =
            _displayExpression.value.contains("TAX+")

        val isMinus =
            _displayExpression.value.contains("TAX-")

        if (isPlus) {

            val prefix =
                _displayExpression.value.substringBefore("TAX+")

            _displayExpression.value =
                prefix + "TAX+" + _display.value + "%"

        } else if (isMinus) {

            val prefix =
                _displayExpression.value.substringBefore("TAX-")

            _displayExpression.value =
                prefix + "TAX-" + _display.value + "%"

        } else {

            /*
             * Fallback.
             */
            _displayExpression.value +=
                _display.value + "%"
        }
    }

    // ---------------------------------------------------------
    // SQUARE ROOT
    // ---------------------------------------------------------

    private fun squareRoot() {

        if (enteringTaxRate) {
            return
        }

        val number =
            currentNumber().toDoubleOrNull()
                ?: return

        if (number < 0) {
            showError()
            return
        }

        val result = sqrt(number)

        replaceCurrentNumber(
            "√$number"
        )

        _display.value =
            formatNumber(result)

        _justCalculated.value = false
    }

    // ---------------------------------------------------------
    // SQUARE
    // ---------------------------------------------------------

    private fun square() {

        if (enteringTaxRate) {
            return
        }

        val number =
            currentNumber().toDoubleOrNull()
                ?: return

        val result =
            number * number

        replaceCurrentNumber(
            "$number²"
        )

        _display.value =
            formatNumber(result)

        _justCalculated.value = false
    }

    // ---------------------------------------------------------
    // CUBE
    // ---------------------------------------------------------

    private fun cube() {

        if (enteringTaxRate) {
            return
        }

        val number =
            currentNumber().toDoubleOrNull()
                ?: return

        val result =
            number * number * number

        replaceCurrentNumber(
            "$number³"
        )

        _display.value =
            formatNumber(result)

        _justCalculated.value = false
    }

    // ---------------------------------------------------------
    // PLUS / MINUS
    // ---------------------------------------------------------

    private fun toggleSign() {

        if (enteringTaxRate) {
            return
        }

        val number =
            currentNumber().toDoubleOrNull()
                ?: return

        val result = -number
        val formatted = formatNumber(result)

        replaceCurrentNumber(formatted)

        _display.value = formatted
        _displayExpression.value =
            prettyExpression(_expression.value)
    }

    // ---------------------------------------------------------
    // PARENTHESES
    // ---------------------------------------------------------

    private fun openParenthesis() {

        if (_justCalculated.value) {
            clearCurrent()
            _justCalculated.value = false
        }

        if (_expression.value.isEmpty()) {

            _expression.value = "("
            _displayExpression.value = "("
            _display.value = "0"

            return
        }

        val last =
            _expression.value.lastOrNull()

        if (
            last != null &&
            (
                    last.isDigit() ||
                            last == ')' ||
                            last == '²' ||
                            last == '³'
                    )
        ) {

            _expression.value += "×"
            _displayExpression.value += "×"
        }

        _expression.value += "("
        _displayExpression.value += "("

        _display.value = "0"
    }

    private fun closeParenthesis() {

        val open =
            _expression.value.count { it == '(' }

        val close =
            _expression.value.count { it == ')' }

        if (open <= close) {
            return
        }

        val last =
            _expression.value.lastOrNull()

        if (
            last == null ||
            last.isOperator() ||
            last == '(' ||
            last == '%'
        ) {
            return
        }

        _expression.value += ")"
        _displayExpression.value += ")"

        _display.value = currentNumber()
    }

    // ---------------------------------------------------------
    // BACKSPACE
    // ---------------------------------------------------------

    private fun backspace() {

        if (_justCalculated.value) {
            clearCurrent()
            return
        }

        if (_expression.value.isEmpty()) {
            return
        }

        if (enteringTaxRate) {

            /*
             * Remove last tax digit.
             */
            _expression.value =
                _expression.value.dropLast(1)

            if (_display.value.length > 1) {
                _display.value =
                    _display.value.dropLast(1)
            } else {
                _display.value = "0"
            }

            if (
                _displayExpression.value.endsWith("%")
            ) {
                _displayExpression.value =
                    _displayExpression.value.dropLast(1)
            }

            if (
                _displayExpression.value.lastOrNull()
                    ?.isDigit() == true
            ) {
                _displayExpression.value =
                    _displayExpression.value.dropLast(1)
            }

            /*
             * If all tax digits were removed,
             * return to:
             *
             * 100 @ TAX+
             */
            if (
                _expression.value.endsWith("TAX+") ||
                _expression.value.endsWith("TAX-")
            ) {
                _displayExpression.value =
                    _displayExpression.value
                        .removeSuffix("TAX+")

                _displayExpression.value =
                    _displayExpression.value
                        .removeSuffix("TAX-")

                _displayExpression.value =
                    _displayExpression.value
                        .removeSuffix(" @ ")

                enteringTaxRate = false
                _display.value = "0"
            }

            return
        }

        _expression.value =
            _expression.value.dropLast(1)

        _displayExpression.value =
            _displayExpression.value.dropLast(1)

        _display.value =
            currentNumber().ifEmpty { "0" }
    }

    // ---------------------------------------------------------
    // CALCULATE
    // ---------------------------------------------------------

    private fun calculate() {

        if (_expression.value.isBlank()) {
            return
        }

        /*
         * TAX must contain a rate.
         */
        if (enteringTaxRate) {

            if (_display.value == "0") {
                return
            }

            enteringTaxRate = false
        }

        try {

            val internalExpression =
                _expression.value

            val visibleExpression =
                _displayExpression.value

            val result =
                ExpressionEvaluator.evaluate(
                    internalExpression
                )

            val formatted =
                formatNumber(result)

            _history.add(
                "$visibleExpression = $formatted"
            )

            _display.value = formatted

            _expression.value = formatted
            _displayExpression.value = formatted

            _justCalculated.value = true

        } catch (_: Exception) {

            showError()
        }
    }

    // ---------------------------------------------------------
    // SYNC NUMBER
    // ---------------------------------------------------------

    private fun syncDisplayToExpression() {

        if (_expression.value.isEmpty()) {

            _expression.value =
                _display.value

            _displayExpression.value =
                _display.value

            return
        }

        val last =
            _expression.value.lastOrNull()

        /*
         * After:
         *
         * 20 %
         *
         * typing 150 should append.
         */
        if (
            last == '%' ||
            last == '(' ||
            last?.isOperator() == true
        ) {

            _expression.value +=
                _display.value

            _displayExpression.value +=
                _display.value

            return
        }

        val match =
            Regex("""\d*\.?\d+$""")
                .find(_expression.value)

        if (match != null) {

            _expression.value =
                _expression.value
                    .removeRange(match.range) +
                        _display.value

            _displayExpression.value =
                _displayExpression.value
                    .removeRange(match.range) +
                        _display.value

        } else {

            _expression.value +=
                _display.value

            _displayExpression.value +=
                _display.value
        }
    }

    // ---------------------------------------------------------
    // CURRENT NUMBER
    // ---------------------------------------------------------

    private fun currentNumber(): String {

        if (_expression.value.isEmpty()) {
            return _display.value
        }

        return Regex("""[-+]?\d*\.?\d+$""")
            .find(_expression.value)
            ?.value
            ?: _display.value
    }

    // ---------------------------------------------------------
    // REPLACE NUMBER
    // ---------------------------------------------------------

    private fun replaceCurrentNumber(
        replacement: String
    ) {

        val expression =
            _expression.value

        val match =
            Regex("""[-+]?\d*\.?\d+$""")
                .find(expression)

        if (match != null) {

            _expression.value =
                expression.removeRange(match.range) +
                        replacement

            _displayExpression.value =
                _displayExpression.value
                    .removeRange(match.range) +
                        replacement

        } else {

            _expression.value += replacement
            _displayExpression.value += replacement
        }
    }

    // ---------------------------------------------------------
    // PRETTY EXPRESSION
    // ---------------------------------------------------------

    private fun prettyExpression(
        value: String
    ): String {

        return value
            .replace("TAX+", " @ TAX+")
            .replace("TAX-", " @ TAX-")
    }

    // ---------------------------------------------------------
    // FORMAT
    // ---------------------------------------------------------

    private fun formatNumber(
        value: Double
    ): String {

        if (
            value.isNaN() ||
            value.isInfinite()
        ) {
            return "Error"
        }

        return if (
            value == value.toLong().toDouble()
        ) {
            value.toLong().toString()
        } else {
            value.toString()
                .trimEnd('0')
                .trimEnd('.')
        }
    }

    // ---------------------------------------------------------
    // CLEAR
    // ---------------------------------------------------------

    private fun clear() {

        _display.value = "0"
        _expression.value = ""
        _displayExpression.value = ""

        _justCalculated.value = false

        enteringTaxRate = false

        _history.clear()
    }

    private fun clearCurrent() {

        _display.value = "0"
        _expression.value = ""
        _displayExpression.value = ""

        enteringTaxRate = false
    }

    // ---------------------------------------------------------
    // ERROR
    // ---------------------------------------------------------

    private fun showError() {

        _display.value = "Error"

        _expression.value = ""
        _displayExpression.value = ""

        _justCalculated.value = true

        enteringTaxRate = false
    }

    // ---------------------------------------------------------
    // OPERATOR
    // ---------------------------------------------------------

    private fun Char.isOperator(): Boolean {

        return this == '+' ||
                this == '-' ||
                this == '×' ||
                this == '÷'
    }
}