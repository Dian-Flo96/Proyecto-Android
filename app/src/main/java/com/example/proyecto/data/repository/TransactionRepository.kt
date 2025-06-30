package com.example.proyecto.data.repository

import com.example.proyecto.data.dao.TransactionDao
import com.example.proyecto.data.model.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    // We can add other methods like update, delete, getById later if needed
}
