package com.example.proyecto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.model.Transaction
import com.example.proyecto.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class MainViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {

    val allTransactions: StateFlow<List<Transaction>> =
        transactionRepository.allTransactions
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList()
            )

    val ingresos: StateFlow<List<Transaction>> = allTransactions
        .map { transactions ->
            transactions.filter { it.tipo == "Ingreso" }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    val gastos: StateFlow<List<Transaction>> = allTransactions
        .map { transactions ->
            transactions.filter { it.tipo == "Gasto" }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun addTransaction(tipo: String, categoria: String, fecha: Long, monto: Double, descripcion: String) {
        viewModelScope.launch {
            val newTransaction = Transaction(
                tipo = tipo,
                categoria = categoria,
                fecha = fecha,
                monto = monto,
                descripcion = descripcion
            )
            transactionRepository.insertTransaction(newTransaction)
        }
    }

    // Function to update an existing transaction
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.updateTransaction(transaction) // Assuming this method exists in repository
        }
    }

    // Function to delete a transaction
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction) // Assuming this method exists in repository
        }
    }

    // --- Budget Goal related ---

    private val _budgetGoal = MutableStateFlow(0.0)
    val budgetGoal: StateFlow<Double> = _budgetGoal.asStateFlow()

    fun updateBudgetGoal(newGoal: Double) {
        _budgetGoal.value = if (newGoal > 0) newGoal else 0.0
    }

    private fun getTransactionsForCurrentMonth(transactions: List<Transaction>): List<Transaction> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        return transactions.filter { transaction ->
            calendar.timeInMillis = transaction.fecha
            calendar.get(Calendar.MONTH) == currentMonth && calendar.get(Calendar.YEAR) == currentYear
        }
    }

    val currentMonthIncome: StateFlow<Double> = allTransactions
        .map { transactions ->
            getTransactionsForCurrentMonth(transactions)
                .filter { it.tipo == "Ingreso" }
                .sumOf { it.monto }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = 0.0
        )

    val currentMonthExpenses: StateFlow<Double> = allTransactions
        .map { transactions ->
            getTransactionsForCurrentMonth(transactions)
                .filter { it.tipo == "Gasto" }
                .sumOf { it.monto }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = 0.0
        )

    val currentMonthNetSavings: StateFlow<Double> =
        combine(currentMonthIncome, currentMonthExpenses) { income, expenses ->
            income - expenses
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = 0.0
        )
}