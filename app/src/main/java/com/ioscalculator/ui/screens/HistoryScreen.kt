
package com.ioscalculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ioscalculator.R
import com.ioscalculator.viewmodel.CalculatorViewModel

@Composable
fun HistoryScreen(
    viewModel: CalculatorViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Load colors from colors.xml
    val screenBackground = colorResource(R.color.calculator_background)
    val operatorColor = colorResource(R.color.calculator_operator_button)
    val whiteColor = colorResource(R.color.calculator_white)
    val lightGray = colorResource(R.color.calculator_light_gray)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
            .padding(20.dp)
    ) {

        // Header and navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextButton(onClick = onBack) {
                Text(
                    text = stringResource(R.string.back_button),
                    color = operatorColor
                )
            }

            Text(
                text = stringResource(R.string.history_title),
                color = whiteColor,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

            TextButton(
                onClick = { viewModel.clearHistory() },
                enabled = state.history.isNotEmpty()
            ) {
                Text(
                    text = stringResource(R.string.clear_history),
                    color = operatorColor
                )
            }
        }

        // Empty history message
        if (state.history.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.empty_history),
                    color = lightGray,
                    fontSize = 18.sp
                )
            }

        } else {

            // Display calculation history
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {

                items(state.history) { item ->

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp)
                    ) {

                        Text(
                            text = item.expression,
                            color = lightGray,
                            fontSize = 17.sp
                        )

                        Text(
                            text = item.result,
                            color = whiteColor,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
