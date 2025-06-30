package com.example.proyecto.ui // <<<< ENSURE THIS IS THE PACKAGE

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.model.Transaction
import com.example.proyecto.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {

    val allTransactions: StateFlow<List<Transaction>> =
        transactionRepository.allTransactions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
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
                // id will be auto-generated
            )
            transactionRepository.insertTransaction(newTransaction)
        }
    }
}