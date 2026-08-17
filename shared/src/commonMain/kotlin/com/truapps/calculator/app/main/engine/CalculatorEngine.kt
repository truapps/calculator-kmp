package com.truapps.calculator.app.main.engine

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type

class CalculatorEngine {

    private val _display: MutableState<String> =
        mutableStateOf("0")

    val display: State<String> = _display

    /*
     * This is now the SINGLE expression.
     *
     * Example:
     *
     * 100 @ TAX+18%
     *
     * The UI displays exactly this expression.
     *
     * ExpressionEvaluator converts the TAX syntax
     * internally before calculating.
     */
    private val _expression: MutableState<String> =
        mutableStateOf("")

    val expression: State<String> = _expression

    private val _justCalculated: MutableState<Boolean> =
        mutableStateOf(false)

    val justCalculated: State<Boolean> =
        _justCalculated

    private val _history: SnapshotStateList<String> =
        mutableStateListOf()

    val history: List<String> = _history

    /*
     * Cursor position in the visible expression.
     *
     * Example:
     *
     * 100 + 25
     *       ↑
     *
     * cursorPosition = 6
     */
    private val _cursorPosition: MutableState<Int> = mutableStateOf(0)

    val cursorPosition: State<Int> = _cursorPosition

    /*
     * True while entering:
     *
     * TAX+18%
     *       ↑
     *
     * or
     *
     * TAX-18%
     */
    private var enteringTaxRate = false

    // =========================================================
    // PUBLIC
    // =========================================================

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

            CalculatorKey.Percentage -> {
                percentage()
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

            CalculatorKey.Left -> {
                moveCursorLeft()
            }

            CalculatorKey.Right -> {
                moveCursorRight()
            }
        }
    }
    fun getCalculatorKey(
        event: KeyEvent
    ): CalculatorKey? {

        if (event.type != KeyEventType.KeyDown) {
            return null
        }
        println("KeyEvent: $event")

        if(event.isShiftPressed) {

            return when (event.key) {
                // Operators
                Key.Two -> CalculatorKey.Square
                Key.Three -> CalculatorKey.Cube
                Key.Four -> CalculatorKey.SqRoot
                Key.Five -> CalculatorKey.Percentage
                Key.Eight -> CalculatorKey.Multiply
                Key.Nine -> CalculatorKey.OpenParenthesis
                Key.Zero -> CalculatorKey.CloseParenthesis
                Key.Minus,Key.NumPadSubtract -> CalculatorKey.PlusMinus
                Key.Equals -> CalculatorKey.Add
                Key.NumPadDelete-> CalculatorKey.Backspace
                Key.T-> CalculatorKey.TaxPlus
                else -> null

            }
        }
        if(event.isAltPressed) {
            return when (event.key) {
                Key.T-> CalculatorKey.TaxMinus
                else -> null
            }
        }
        return when (event.key) {


            // Numbers
            Key.One -> CalculatorKey.Digit(1)
            Key.Two ->  CalculatorKey.Digit(2)
            Key.Three -> CalculatorKey.Digit(3)
            Key.Four -> CalculatorKey.Digit(4)
            Key.Five -> CalculatorKey.Digit(5)
            Key.Six -> CalculatorKey.Digit(6)
            Key.Seven -> CalculatorKey.Digit(7)
            Key.Eight -> CalculatorKey.Digit(8)
            Key.Nine ->  CalculatorKey.Digit(9)
            Key.Zero ->  CalculatorKey.Digit(0)

            // Operators
            Key.Plus, Key.NumPadEnter -> CalculatorKey.Add
            Key.Minus, Key.NumPadSubtract -> CalculatorKey.Subtract

            Key.Multiply,Key.NumPadMultiply -> CalculatorKey.Multiply
            Key.Slash,Key.NumPadDivide -> CalculatorKey.Divide

            // Decimal
            Key.Period,Key.NumPadDelete -> CalculatorKey.Decimal

            // Enter
            Key.Enter, Key.NumPadEnter -> CalculatorKey.Equals

            // Backspace
            Key.Backspace -> CalculatorKey.Backspace

            // Parentheses
            Key.LeftBracket -> CalculatorKey.OpenParenthesis
            Key.RightBracket -> CalculatorKey.CloseParenthesis

            // Escape
            Key.Escape,Key.Delete -> CalculatorKey.Clear

            // Cursor
            Key.DirectionLeft -> CalculatorKey.Left
            Key.DirectionRight -> CalculatorKey.Right
            else -> null
        }
    }

    // =========================================================
    // CURSOR
    // =========================================================

    fun setCursorPosition(position: Int) {

        _cursorPosition.value = position.coerceIn(
            0,
            _expression.value.length
        )
    }

    /*
     * Called when TextFieldValue changes.
     *
     * Important:
     * We don't modify the cursor here.
     */
    fun setExpression(value: String) {

        _expression.value = value

        _cursorPosition.value =
            _cursorPosition.value.coerceIn(
                0,
                value.length
            )

        updateDisplayFromExpression()
    }

    private fun moveCursorLeft() {

        if (_cursorPosition.value > 0) {
            _cursorPosition.value--
        }
    }

    private fun moveCursorRight() {

        if (_cursorPosition.value < _expression.value.length) {
            _cursorPosition.value++
        }
    }

    // =========================================================
    // DIGIT
    // =========================================================

    private fun inputDigit(digit: Int) {

        if (enteringTaxRate) {

            inputTaxDigit(digit)

            return
        }

        if (_justCalculated.value) {

            clearCurrent()

            _justCalculated.value = false
        }

        insertAtCursor(
            digit.toString()
        )

        updateDisplayFromExpression()
    }

    // =========================================================
    // DECIMAL
    // =========================================================

    private fun inputDecimal() {

        if (enteringTaxRate) {

            inputTaxDecimal()

            return
        }

        if (_justCalculated.value) {

            clearCurrent()

            _justCalculated.value = false
        }

        /*
         * Find the number around the cursor.
         *
         * Don't allow:
         *
         * 12.3.4
         */
        val number = numberAroundCursor()

        if (number.contains(".")) {
            return
        }

        /*
         * If cursor is at beginning of a number,
         * insert 0.
         */
        if (
            _cursorPosition.value == 0 || _expression.value
                .getOrNull(_cursorPosition.value - 1)
                ?.isDigit()==false

        ) {

            insertAtCursor("0.")

        } else {

            insertAtCursor(".")
        }

        updateDisplayFromExpression()
    }

    // =========================================================
    // TAX
    // =========================================================

    private fun startTax(plus: Boolean) {

        if (_justCalculated.value) {

            /*
             * Allow:
             *
             * 118
             *
             * TAX+
             *
             * 118 @ TAX+
             */
            _justCalculated.value = false
        }

        if (_expression.value.isEmpty()) {

            insertAtCursor(
                _display.value
            )
        }

        /*
         * TAX should be applied to a value,
         * not directly after an operator.
         */
        val previous =
            _expression.value
                .getOrNull(
                    _cursorPosition.value - 1
                )

        if (
            previous == null ||
            previous.isOperator() ||
            previous == '('
        ) {
            return
        }

        /*
         * Prevent:
         *
         * 100 @ TAX+ @ TAX+
         */
        if (
            _expression.value
                .substring(0, _cursorPosition.value)
                .endsWith(" @ TAX+") ||
            _expression.value
                .substring(0, _cursorPosition.value)
                .endsWith(" @ TAX-")
        ) {
            return
        }

        val taxText =
            if (plus) {
                " @ TAX+"
            } else {
                " @ TAX-"
            }

        insertAtCursor(taxText)

        enteringTaxRate = true

        _display.value = "0"
    }

    private fun inputTaxDigit(digit: Int) {

        val expression =
            _expression.value

        val beforeCursor =
            expression.substring(
                0,
                _cursorPosition.value
            )

        val taxStartPlus =
            beforeCursor.lastIndexOf(
                " @ TAX+"
            )

        val taxStartMinus =
            beforeCursor.lastIndexOf(
                " @ TAX-"
            )

        val taxStart =
            maxOf(
                taxStartPlus,
                taxStartMinus
            )

        if (taxStart == -1) {

            enteringTaxRate = false

            inputDigit(digit)

            return
        }

        val taxPrefixEnd =
            taxStart +
                    if (taxStartPlus > taxStartMinus) {
                        " @ TAX+".length
                    } else {
                        " @ TAX-".length
                    }

        /*
         * Existing tax rate.
         *
         * Example:
         *
         * 100 @ TAX+18%
         *
         * rate = 18
         */
        var rate =
            expression
                .substring(
                    taxPrefixEnd,
                    _cursorPosition.value
                )
                .removeSuffix("%")

        /*
         * If cursor is before %, don't include it.
         */
        rate =
            rate.filter {
                it.isDigit() || it == '.'
            }

        rate += digit.toString()

        val before =
            expression.substring(
                0,
                taxPrefixEnd
            )

        val afterStart =
            if (
                expression
                    .getOrNull(_cursorPosition.value) == '%'
            ) {
                _cursorPosition.value + 1
            } else {
                _cursorPosition.value
            }

        val after =
            expression.substring(
                afterStart
            )

        val newExpression =
            before +
                    rate +
                    "%" +
                    after

        _expression.value =
            newExpression

        _cursorPosition.value =
            before.length +
                    rate.length +
                    1

        _display.value = rate
    }

    private fun inputTaxDecimal() {

        val expression =
            _expression.value

        val beforeCursor =
            expression.substring(
                0,
                _cursorPosition.value
            )

        val taxStart =
            maxOf(
                beforeCursor.lastIndexOf(" @ TAX+"),
                beforeCursor.lastIndexOf(" @ TAX-")
            )

        if (taxStart == -1) {
            return
        }

        val taxMarkerLength =
            if (
                beforeCursor
                    .lastIndexOf(" @ TAX+") >
                beforeCursor
                    .lastIndexOf(" @ TAX-")
            ) {
                " @ TAX+".length
            } else {
                " @ TAX-".length
            }

        val rateStart =
            taxStart + taxMarkerLength

        val rate =
            expression
                .substring(
                    rateStart,
                    _cursorPosition.value
                )
                .removeSuffix("%")

        if (rate.contains(".")) {
            return
        }

        val before =
            expression.substring(
                0,
                _cursorPosition.value
            )

        val after =
            expression.substring(
                _cursorPosition.value
            )

        _expression.value =
            before + "." + after

        _cursorPosition.value++

        _display.value =
            rate + "."
    }

    // =========================================================
    // OPERATORS
    // =========================================================

    private fun inputOperator(
        operator: String
    ) {

        if (enteringTaxRate) {

            if (_display.value == "0") {
                return
            }

            enteringTaxRate = false
        }

        if (_expression.value.isEmpty()) {

            insertAtCursor(
                _display.value
            )
        }

        val previous =
            _expression.value
                .getOrNull(
                    _cursorPosition.value - 1
                )

        /*
         * Replace an existing operator.
         */
        if (
            previous != null &&
            previous.isOperator()
        ) {

            _expression.value =
                _expression.value
                    .removeRange(
                        _cursorPosition.value - 1,
                        _cursorPosition.value
                    )

            _cursorPosition.value--

            insertAtCursor(operator)

            _display.value = "0"

            return
        }

        /*
         * Don't put an operator immediately
         * after an opening parenthesis.
         */
        if (previous == '(') {
            return
        }

        insertAtCursor(operator)

        _display.value = "0"

        _justCalculated.value = false
    }

    // =========================================================
    // PERCENTAGE
    // =========================================================

    /*
     * Binary percentage:
     *
     * 20 % 150
     *
     * = 30
     */
    private fun percentage() {

        if (enteringTaxRate) {
            return
        }

        if (_expression.value.isEmpty()) {

            insertAtCursor(
                _display.value
            )
        }

        val previous =
            _expression.value
                .getOrNull(
                    _cursorPosition.value - 1
                )

        if (
            previous == null ||
            previous.isOperator() ||
            previous == '(' ||
            previous == '%'
        ) {
            return
        }

        insertAtCursor("%")

        /*
         * After `%`, the next number becomes
         * the second operand.
         */
        _display.value = "0"

        _justCalculated.value = false
    }

    // =========================================================
    // SQUARE ROOT
    // =========================================================

    private fun squareRoot() {

        if (enteringTaxRate) {
            return
        }

        insertAtCursor("√")

        _justCalculated.value = false

        updateDisplayFromExpression()
    }

    // =========================================================
    // SQUARE
    // =========================================================

    private fun square() {

        if (enteringTaxRate) {
            return
        }

        insertAtCursor("²")

        _justCalculated.value = false

        updateDisplayFromExpression()
    }

    // =========================================================
    // CUBE
    // =========================================================

    private fun cube() {

        if (enteringTaxRate) {
            return
        }

        insertAtCursor("³")

        _justCalculated.value = false

        updateDisplayFromExpression()
    }

    // =========================================================
    // PLUS / MINUS
    // =========================================================

    private fun toggleSign() {

        if (enteringTaxRate) {
            return
        }

        val number =
            numberAroundCursor()

        val value =
            number.toDoubleOrNull()
                ?: return

        val start =
            _cursorPosition.value - number.length

        val replacement =
            if (value < 0) {
                formatNumber(-value)
            } else {
                "-${formatNumber(value)}"
            }

        _expression.value =
            _expression.value
                .removeRange(
                    start,
                    _cursorPosition.value
                )
                .let {
                    it.substring(
                        0,
                        start
                    ) +
                            replacement +
                            it.substring(start)
                }

        _cursorPosition.value =
            start + replacement.length

        updateDisplayFromExpression()
    }

    // =========================================================
    // PARENTHESES
    // =========================================================

    private fun openParenthesis() {

        if (_justCalculated.value) {

            clearCurrent()

            _justCalculated.value = false
        }

        val previous =
            _expression.value
                .getOrNull(
                    _cursorPosition.value - 1
                )

        if (
            previous != null &&
            (
                    previous.isDigit() ||
                            previous == ')' ||
                            previous == '²' ||
                            previous == '³'
                    )
        ) {

            insertAtCursor("×")
        }

        insertAtCursor("(")

        _display.value = "0"
    }

    private fun closeParenthesis() {

        val before =
            _expression.value
                .substring(
                    0,
                    _cursorPosition.value
                )

        val open =
            before.count {
                it == '('
            }

        val close =
            before.count {
                it == ')'
            }

        if (open <= close) {
            return
        }

        val previous =
            _expression.value
                .getOrNull(
                    _cursorPosition.value - 1
                )

        if (
            previous == null ||
            previous.isOperator() ||
            previous == '('
        ) {
            return
        }

        insertAtCursor(")")

        updateDisplayFromExpression()
    }

    // =========================================================
    // BACKSPACE
    // =========================================================

    private fun backspace() {

        if (_expression.value.isEmpty()) {
            return
        }

        if (_cursorPosition.value <= 0) {
            return
        }

        val position =
            _cursorPosition.value.coerceIn(
                0,
                _expression.value.length
            )

        /*
         * If deleting `%` from TAX rate:
         *
         * 100 @ TAX+18%
         *               ↑
         *
         * Don't leave:
         *
         * 100 @ TAX+18
         *
         * We remove the rate digit as well.
         */
        if (
            enteringTaxRate &&
            _expression.value
                .getOrNull(position - 1) == '%'
        ) {

            val taxStart =
                findTaxStart(position)

            if (taxStart != -1) {

                val rateStart =
                    findTaxRateStart(taxStart)

                if (rateStart < position - 1) {

                    _expression.value =
                        _expression.value
                            .removeRange(
                                position - 2,
                                position
                            )

                    _cursorPosition.value =
                        position - 2

                    _display.value =
                        taxRateText(
                            rateStart
                        )

                    return
                }
            }
        }

        _expression.value =
            _expression.value.removeRange(
                position - 1,
                position
            )

        _cursorPosition.value =
            position - 1

        updateDisplayFromExpression()
    }

    // =========================================================
    // CALCULATE
    // =========================================================

    private fun calculate() {

        if (_expression.value.isBlank()) {
            return
        }

        /*
         * Don't calculate incomplete tax:
         *
         * 100 @ TAX+
         */
        if (
            enteringTaxRate &&
            taxRateText(findTaxStart(_cursorPosition.value))
                .isBlank()
        ) {
            return
        }

        try {

            val currentExpression =
                _expression.value

            val result =
                ExpressionEvaluator.evaluate(
                    currentExpression
                )

            val formatted =
                formatNumber(result)

            _history.add(
                "$currentExpression = $formatted"
            )

            _display.value =
                formatted

            _expression.value =
                formatted

            _cursorPosition.value =
                formatted.length

            enteringTaxRate = false

            _justCalculated.value = true

        } catch (_: Exception) {

            _display.value = "Error"

            _justCalculated.value = true
        }
    }

    // =========================================================
    // INSERT
    // =========================================================

    private fun insertAtCursor(
        value: String
    ) {

        val expression =
            _expression.value

        /*
         * IMPORTANT:
         *
         * The cursor can come from Compose's
         * TextFieldValue.
         *
         * Always clamp it before substring().
         */
        val position =
            _cursorPosition.value.coerceIn(
                0,
                expression.length
            )

        _expression.value =
            expression.substring(
                0,
                position
            ) +
                    value +
                    expression.substring(
                        position
                    )

        _cursorPosition.value =
            position + value.length
    }

    // =========================================================
    // CURRENT NUMBER
    // =========================================================

    private fun currentNumber(): String {

        if (_expression.value.isEmpty()) {
            return _display.value
        }

        return numberAroundCursor()
            .ifEmpty {
                _display.value
            }
    }

    private fun numberAroundCursor(): String {

        val expression =
            _expression.value

        if (expression.isEmpty()) {
            return ""
        }

        val cursor =
            _cursorPosition.value.coerceIn(
                0,
                expression.length
            )

        var start = cursor
        var end = cursor

        while (
            start > 0 &&
            (
                    expression[start - 1].isDigit() ||
                            expression[start - 1] == '.'
                    )
        ) {
            start--
        }

        while (
            end < expression.length &&
            (
                    expression[end].isDigit() ||
                            expression[end] == '.'
                    )
        ) {
            end++
        }

        return expression.substring(
            start,
            end
        )
    }

    // =========================================================
    // DISPLAY
    // =========================================================

    private fun updateDisplayFromExpression() {

        val number =
            currentNumber()

        if (number.isNotEmpty()) {
            _display.value = number
        } else {
            _display.value = "0"
        }
    }

    // =========================================================
    // TAX HELPERS
    // =========================================================

    private fun findTaxStart(
        position: Int
    ): Int {

        val before =
            _expression.value.substring(
                0,
                position.coerceIn(
                    0,
                    _expression.value.length
                )
            )

        return maxOf(
            before.lastIndexOf(" @ TAX+"),
            before.lastIndexOf(" @ TAX-")
        )
    }

    private fun findTaxRateStart(
        taxStart: Int
    ): Int {

        if (taxStart == -1) {
            return -1
        }

        val marker =
            if (
                _expression.value
                    .startsWith(
                        " @ TAX+",
                        taxStart
                    )
            ) {
                " @ TAX+"
            } else {
                " @ TAX-"
            }

        return taxStart + marker.length
    }

    private fun taxRateText(
        taxStart: Int
    ): String {

        if (taxStart == -1) {
            return ""
        }

        val rateStart =
            findTaxRateStart(taxStart)

        if (rateStart == -1) {
            return ""
        }

        return _expression.value
            .substring(
                rateStart
            )
            .removeSuffix("%")
            .filter {
                it.isDigit() || it == '.'
            }
    }

    // =========================================================
    // CLEAR
    // =========================================================

    private fun clear() {

        _expression.value = ""
        _display.value = "0"

        _cursorPosition.value = 0

        _justCalculated.value = false

        enteringTaxRate = false

        _history.clear()
    }

    private fun clearCurrent() {

        _expression.value = ""
        _display.value = "0"

        _cursorPosition.value = 0

        enteringTaxRate = false
    }

    // =========================================================
    // FORMAT
    // =========================================================

    private fun formatNumber(
        value: Double
    ): String {

        if (
            value.isNaN() ||
            value.isInfinite()
        ) {
            return "Error"
        }

        if (
            value ==
            value.toLong().toDouble()
        ) {
            return value.toLong().toString()
        }

        return value
            .toString()
            .trimEnd('0')
            .trimEnd('.')
    }

    // =========================================================
    // CHAR HELPERS
    // =========================================================

    private fun Char.isOperator(): Boolean {

        return this == '+' ||
                this == '-' ||
                this == '×' ||
                this == '÷'
    }
}