package com.example.proyecto.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

// Helper to format currency (can be moved to a common utils file later)
private fun formatCurrency(amount: Double): String {
    val numberFormat =
        NumberFormat.getCurrencyInstance(Locale("es", "PE")) // Adjust locale as needed
    return numberFormat.format(amount)
}

// Helper to get current month name (can be moved)
private fun getCurrentMonthName(): String {
    val calendar = Calendar.getInstance()
    return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        ?: "Mes Actual"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AhorroScreen(viewModel: MainViewModel) {
    val budgetGoal by viewModel.budgetGoal.collectAsState()
    val currentMonthIncome by viewModel.currentMonthIncome.collectAsState()
    val currentMonthExpenses by viewModel.currentMonthExpenses.collectAsState()
    val currentMonthNetSavings by viewModel.currentMonthNetSavings.collectAsState()

    var goalInputText by remember { mutableStateOf(budgetGoal.takeIf { it > 0 }?.toString() ?: "") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(budgetGoal) { // Update text field if goal changes externally
        if (budgetGoal > 0) {
            goalInputText = budgetGoal.toString()
        } else {
            goalInputText = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Meta de Ahorro Mensual (${getCurrentMonthName()})",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Goal Setting
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = goalInputText,
                onValueChange = { goalInputText = it },
                label = { Text("Monto Objetivo S/.") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    goalInputText.toDoubleOrNull()?.let { viewModel.updateBudgetGoal(it) }
                    focusManager.clearFocus()
                }),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                goalInputText.toDoubleOrNull()?.let { viewModel.updateBudgetGoal(it) }
                focusManager.clearFocus()
            }) {
                Text("Fijar")
            }
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Progress Summary
        Text(
            "Resumen del Mes",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        SummaryRow("Meta de Ahorro:", formatCurrency(budgetGoal))
        SummaryRow("Ingresos del Mes:", formatCurrency(currentMonthIncome))
        SummaryRow("Gastos del Mes:", formatCurrency(currentMonthExpenses))
        SummaryRow(
            "Ahorro Neto del Mes:",
            formatCurrency(currentMonthNetSavings),
            isPositive = currentMonthNetSavings >= 0
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Bar and remaining
        if (budgetGoal > 0) {
            val progress = (currentMonthNetSavings / budgetGoal).toFloat().coerceIn(0f, 1f)
            val remainingToGoal = budgetGoal - currentMonthNetSavings

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (currentMonthNetSavings >= budgetGoal) "¡Meta Alcanzada! (${
                    formatCurrency(
                        currentMonthNetSavings - budgetGoal
                    )
                } extra)"
                else "${formatCurrency(remainingToGoal)} para alcanzar la meta.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Text(
                "Fija una meta de ahorro para ver tu progreso.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isPositive: Boolean? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = when (isPositive) {
                true -> MaterialTheme.colorScheme.primary // Or a specific green
                false -> MaterialTheme.colorScheme.error
                null -> LocalContentColor.current
            },
            textAlign = TextAlign.End
        )
    }
}