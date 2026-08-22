package com.truapps.digical.main.engine

sealed interface CalculatorKey {

    data class Digit(val value: Int) : CalculatorKey

    data object Decimal : CalculatorKey
    data object Add : CalculatorKey
    data object Subtract : CalculatorKey
    data object Multiply : CalculatorKey
    data object SqRoot : CalculatorKey
    data object Square : CalculatorKey
    data object Cube: CalculatorKey
    data object Divide : CalculatorKey

    data object OpenParenthesis : CalculatorKey
    data object CloseParenthesis : CalculatorKey

    data object PlusMinus : CalculatorKey
    data object Percentage : CalculatorKey

    data object TaxPlus : CalculatorKey
    data object TaxMinus : CalculatorKey

    data object Backspace : CalculatorKey
    data object Clear : CalculatorKey
    data object Equals : CalculatorKey

    data object Left : CalculatorKey
    data object Right : CalculatorKey
}