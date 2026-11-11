
package com.ioscalculator.model

import com.ioscalculator.logic.CalculatorOperation

data class HistoryItem(
    val expression: String,
    val result: String
)

enum class CalculatorError {
    DIVISION_BY_ZERO,
    INVALID_INPUT,
    INVALID_RESULT
}

data class CalculatorState(
    val displayValue: String = "0",
    val firstOperand: Double? = null,
    val operation: CalculatorOperation? = null,
    val isNewInput: Boolean = true,
    val expression: String = "",
    val error: CalculatorError? = null,
    val history: List<HistoryItem> = emptyList(),
    val lastOperand: Double? = null,
    val lastOperation: CalculatorOperation? = null
)
