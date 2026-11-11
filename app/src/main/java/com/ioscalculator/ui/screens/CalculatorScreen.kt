package com.ioscalculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioscalculator.logic.CalculatorOperation
import com.ioscalculator.model.CalculatorError
import com.ioscalculator.ui.components.CalculatorButton
import com.ioscalculator.viewmodel.CalculatorViewModel
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.ioscalculator.R

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    onOpenHistory: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val screenBackground = colorResource(R.color.calculator_background)
    val numberColor = colorResource(R.color.calculator_number_button)
    val functionColor = colorResource(R.color.calculator_function_button)
    val operatorColor = colorResource(R.color.calculator_operator_button)
    val whiteColor = colorResource(R.color.calculator_white)
    val lightGray = colorResource(R.color.calculator_light_gray)

    val displayText = when (state.error) {
        CalculatorError.DIVISION_BY_ZERO ->
            stringResource(R.string.error_division_by_zero)

        CalculatorError.INVALID_INPUT ->
            stringResource(R.string.error_invalid_input)

        CalculatorError.INVALID_RESULT ->
            stringResource(R.string.error_invalid_result)

        null -> state.displayValue
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onOpenHistory) {
                Text(
                    text = stringResource(R.string.history_title)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = state.expression,
            modifier = Modifier.fillMaxWidth(),
            color = Color.LightGray,
            fontSize = 21.sp,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = displayText,
            modifier = Modifier.fillMaxWidth(),
            color = whiteColor,
            fontSize = 60.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(28.dp))

        val buttonRows = listOf(
            listOf(
                stringResource(R.string.button_clear),
                stringResource(R.string.button_sign),
                stringResource(R.string.button_percentage),
                stringResource(R.string.button_divide)
            ),
            listOf("7", "8", "9", stringResource(R.string.button_multiply)),
            listOf("4", "5", "6", stringResource(R.string.button_subtract)),
            listOf("1", "2", "3", stringResource(R.string.button_add)),
            listOf("0", stringResource(R.string.button_decimal), stringResource(R.string.button_equals))
        )

        buttonRows.forEachIndexed { rowIndex, buttons ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                buttons.forEach { buttonText ->

                    val isOperator = buttonText in listOf(
                        "÷", "×", "−", "+", "="
                    )

                    val isFunction = buttonText in listOf(
                        "AC", "+/-", "%"
                    )

                    val buttonBackground = when {
                        isOperator -> operatorColor
                        isFunction -> functionColor
                        else -> numberColor
                    }

                    val buttonTextColor = if (isFunction) {
                        Color.Black
                    } else {
                        whiteColor
                    }

                    val buttonModifier = if (
                        rowIndex == 4 && buttonText == "0"
                    ) {
                        Modifier.weight(2f)
                    } else {
                        Modifier.weight(1f)
                    }

                    CalculatorButton(
                        text = buttonText,
                        backgroundColor = buttonBackground,
                        contentColor = buttonTextColor,
                        modifier = buttonModifier,
                        onClick = {
                            when (buttonText) {
                                "AC" -> viewModel.clear()
                                "+/-" -> viewModel.toggleSign()
                                "%" -> viewModel.applyPercentage()
                                "." -> viewModel.appendDecimal()
                                "=" -> viewModel.calculate()
                                "÷" -> viewModel.selectOperation(
                                    CalculatorOperation.DIVIDE
                                )
                                "×" -> viewModel.selectOperation(
                                    CalculatorOperation.MULTIPLY
                                )
                                "−" -> viewModel.selectOperation(
                                    CalculatorOperation.SUBTRACT
                                )
                                "+" -> viewModel.selectOperation(
                                    CalculatorOperation.ADD
                                )
                                else -> viewModel.appendDigit(buttonText)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

private val sixtySp = 60.sp
