package com.example.proyecto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.model.PromotionItem
import com.example.proyecto.data.repository.GeminiRecommendationServiceRepository
import com.example.proyecto.data.repository.SearchServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Sealed class for UI states
sealed class PromocionesUiState {
    object Idle : PromocionesUiState()
    object Loading : PromocionesUiState()
    data class Success(val promotions: List<PromotionItem>) : PromocionesUiState()
    data class Error(val message: String) : PromocionesUiState()
}

open class PromocionesViewModel(
    private val searchService: SearchServiceRepository, 
    private val geminiService: GeminiRecommendationServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PromocionesUiState>(PromocionesUiState.Idle)
    val uiState: StateFlow<PromocionesUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun searchPromotions() {
        val currentQuery = _searchQuery.value
        if (currentQuery.isBlank()) {
            _uiState.value = PromocionesUiState.Error("Search query cannot be empty.")
            return
        }

        viewModelScope.launch {
            _uiState.value = PromocionesUiState.Loading
            try {
                // Step 1: Fetch raw results from the mock search service
                val rawResults = searchService.fetchRawResults(currentQuery)

                // Step 2: Send raw results to the mock Gemini service for recommendations
                val geminiRecommendations = geminiService.getRecommendations(currentQuery, rawResults)
                
                if (geminiRecommendations.isEmpty() && rawResults.isNotEmpty()) {
                    // This case can happen if Gemini mock filters out all raw results
                     _uiState.value = PromocionesUiState.Success(emptyList()) // Or a specific "no recommendations from AI" message
                } else {
                    _uiState.value = PromocionesUiState.Success(geminiRecommendations)
                }

            } catch (e: Exception) { 
                // Log the exception e
                _uiState.value = PromocionesUiState.Error("Demo error: ${e.message}")
            }
        }
    }
}
