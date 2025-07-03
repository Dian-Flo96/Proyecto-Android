package com.example.proyecto.data.repository

import com.example.proyecto.data.dao.TransactionDao
import com.example.proyecto.data.model.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction) // Assuming 'update' is the method name in TransactionDao
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction) // Assuming 'delete' is the method name in TransactionDao
    }

    // We can add other methods like getById later if needed
}
