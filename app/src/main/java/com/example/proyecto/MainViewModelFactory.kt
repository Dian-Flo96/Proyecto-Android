package com.example.proyecto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto.data.repository.TransactionRepository
import com.example.proyecto.ui.MainViewModel // Ensure this import is for the ui package

class MainViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel is of type com.example.proyecto.ui.MainViewModel
        if (modelClass.isAssignableFrom(com.example.proyecto.ui.MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Create and return an instance of com.example.proyecto.ui.MainViewModel
            return com.example.proyecto.ui.MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
