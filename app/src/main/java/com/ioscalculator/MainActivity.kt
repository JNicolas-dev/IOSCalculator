
package com.ioscalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ioscalculator.ui.screens.CalculatorScreen
import com.ioscalculator.ui.screens.HistoryScreen
import com.ioscalculator.ui.theme.IOSCalculatorTheme
import com.ioscalculator.viewmodel.CalculatorViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            IOSCalculatorTheme {

                val calculatorViewModel: CalculatorViewModel = viewModel()

                var showHistory by remember {
                    mutableStateOf(false)
                }

                if (showHistory) {

                    HistoryScreen(
                        viewModel = calculatorViewModel,
                        onBack = {
                            showHistory = false
                        }
                    )

                } else {

                    CalculatorScreen(
                        viewModel = calculatorViewModel,
                        onOpenHistory = {
                            showHistory = true
                        }
                    )
                }
            }
        }
    }
}

