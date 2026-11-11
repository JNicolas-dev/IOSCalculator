package com.ioscalculator.logic

enum class CalculatorOperation {
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE
}

sealed class CalculationResult {

    data class Success(val value: Double) : CalculationResult()

    object DivisionByZero : CalculationResult()

    object InvalidResult : CalculationResult()
}

object CalculatorLogic {

    fun calculate(
        firstValue: Double,
        secondValue: Double,
        operation: CalculatorOperation
    ): CalculationResult {

        if (operation == CalculatorOperation.DIVIDE && secondValue == 0.0) {
            return CalculationResult.DivisionByZero
        }

        val result = when (operation) {

            CalculatorOperation.ADD -> firstValue + secondValue

            CalculatorOperation.SUBTRACT -> firstValue - secondValue

            CalculatorOperation.MULTIPLY -> firstValue * secondValue

            CalculatorOperation.DIVIDE -> firstValue / secondValue
        }

        if (!result.isFinite()) {
            return CalculationResult.InvalidResult
        }

        return CalculationResult.Success(result)
    }
}
