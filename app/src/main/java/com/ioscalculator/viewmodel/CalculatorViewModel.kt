
package com.ioscalculator.viewmodel

import androidx.lifecycle.ViewModel
import com.ioscalculator.logic.CalculationResult
import com.ioscalculator.logic.CalculatorLogic
import com.ioscalculator.logic.CalculatorOperation
import com.ioscalculator.model.CalculatorError
import com.ioscalculator.model.CalculatorState
import com.ioscalculator.model.HistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorViewModel : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun appendDigit(digit: String) {
        if (digit !in "0".."9") return

        val current = _state.value

        val newValue = when {
            current.error != null -> digit
            current.isNewInput -> digit
            current.displayValue == "0" -> digit
            else -> current.displayValue + digit
        }

        _state.value = current.copy(
            displayValue = newValue,
            isNewInput = false,
            error = null
        )
    }

    fun appendDecimal() {
        val current = _state.value

        if (current.error != null) {
            _state.value = current.copy(
                displayValue = "0.",
                isNewInput = false,
                error = null
            )
            return
        }

        if (current.isNewInput) {
            _state.value = current.copy(
                displayValue = "0.",
                isNewInput = false
            )
            return
        }

        if (!current.displayValue.contains(".")) {
            _state.value = current.copy(
                displayValue = current.displayValue + "."
            )
        }
    }

    fun selectOperation(operation: CalculatorOperation) {
        var current = _state.value

        if (current.error != null) return

        if (current.operation != null &&
            current.firstOperand != null &&
            !current.isNewInput
        ) {
            calculate()
            current = _state.value

            if (current.error != null) return
        }

        val firstValue = current.displayValue.toDoubleOrNull() ?: return

        _state.value = current.copy(
            firstOperand = firstValue,
            operation = operation,
            isNewInput = true,
            expression = "${formatNumber(firstValue)} ${getSymbol(operation)}",
            lastOperand = null,
            lastOperation = null
        )
    }

    fun calculate() {
        val current = _state.value

        if (current.error != null) return

        val firstValue = current.firstOperand
        val operation = current.operation

        val secondValue: Double
        val actualOperation: CalculatorOperation

        if (firstValue != null && operation != null) {
            secondValue = current.displayValue.toDoubleOrNull() ?: return
            actualOperation = operation
        } else if (
            current.lastOperand != null &&
            current.lastOperation != null
        ) {
            secondValue = current.lastOperand
            actualOperation = current.lastOperation
        } else {
            return
        }

        val initialValue = firstValue
            ?: current.displayValue.toDoubleOrNull()
            ?: return

        val result = CalculatorLogic.calculate(
            initialValue,
            secondValue,
            actualOperation
        )

        when (result) {
            is CalculationResult.Success -> {
                val resultText = formatNumber(result.value)

                val expression =
                    "${formatNumber(initialValue)} " +
                            "${getSymbol(actualOperation)} " +
                            formatNumber(secondValue)

                val newHistory = if (firstValue != null) {
                    listOf(
                        HistoryItem(
                            expression = expression,
                            result = resultText
                        )
                    ) + current.history
                } else {
                    current.history
                }

                _state.value = current.copy(
                    displayValue = resultText,
                    firstOperand = null,
                    operation = null,
                    isNewInput = true,
                    expression = "$expression =",
                    history = newHistory,
                    lastOperand = secondValue,
                    lastOperation = actualOperation,
                    error = null
                )
            }

            CalculationResult.DivisionByZero -> {
                _state.value = current.copy(
                    error = CalculatorError.DIVISION_BY_ZERO,
                    firstOperand = null,
                    operation = null,
                    isNewInput = true
                )
            }

            CalculationResult.InvalidResult -> {
                _state.value = current.copy(
                    error = CalculatorError.INVALID_RESULT,
                    firstOperand = null,
                    operation = null,
                    isNewInput = true
                )
            }
        }
    }

    fun toggleSign() {
        val current = _state.value

        if (current.error != null || current.displayValue == "0") return

        val newValue = if (current.displayValue.startsWith("-")) {
            current.displayValue.drop(1)
        } else {
            "-${current.displayValue}"
        }

        _state.value = current.copy(
            displayValue = newValue
        )
    }


    fun applyPercentage() {
        val current = _state.value

        if (current.error != null) return

        val currentValue = current.displayValue.toDoubleOrNull() ?: return

        val result = when (current.operation) {

            CalculatorOperation.ADD,
            CalculatorOperation.SUBTRACT -> {
                val baseValue = current.firstOperand ?: return
                baseValue * currentValue / 100.0
            }

            CalculatorOperation.MULTIPLY,
            CalculatorOperation.DIVIDE,
            null -> {
                currentValue / 100.0
            }
        }

        if (!result.isFinite()) {
            _state.value = current.copy(
                error = CalculatorError.INVALID_RESULT
            )
            return
        }

        _state.value = current.copy(
            displayValue = formatNumber(result),
            isNewInput = false,
            error = null
        )
    }


    fun clear() {
        val current = _state.value

        _state.value = CalculatorState(
            history = current.history
        )
    }

    fun clearHistory() {
        _state.value = _state.value.copy(
            history = emptyList()
        )
    }

    private fun getSymbol(operation: CalculatorOperation): String {
        return when (operation) {
            CalculatorOperation.ADD -> "+"
            CalculatorOperation.SUBTRACT -> "−"
            CalculatorOperation.MULTIPLY -> "×"
            CalculatorOperation.DIVIDE -> "÷"
        }
    }

    private fun formatNumber(value: Double): String {
        if (!value.isFinite()) return "0"

        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            java.math.BigDecimal(value.toString())
                .stripTrailingZeros()
                .toPlainString()
        }
    }
}
