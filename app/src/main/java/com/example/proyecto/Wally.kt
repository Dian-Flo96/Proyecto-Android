package com.example.proyecto

import android.app.Application
import com.example.proyecto.data.AppDatabase
import com.example.proyecto.data.repository.TransactionRepository

class Wally : Application() {

    // Lazy initialization for the database and repository
    val database by lazy { AppDatabase.getDatabase(this) }
    val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }

}
