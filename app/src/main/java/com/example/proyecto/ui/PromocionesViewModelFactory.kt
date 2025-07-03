package com.example.proyecto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto.data.repository.GeminiRecommendationServiceRepository
import com.example.proyecto.data.repository.SearchServiceRepository

// This factory will now use the mock repositories for the demo
class PromocionesViewModelFactory(
    private val searchService: SearchServiceRepository, 
    private val geminiService: GeminiRecommendationServiceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PromocionesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PromocionesViewModel(searchService, geminiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
