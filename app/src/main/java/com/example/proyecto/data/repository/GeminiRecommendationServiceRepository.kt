package com.example.proyecto.data.repository

import com.example.proyecto.data.model.PromotionItem

interface GeminiRecommendationServiceRepository {
    suspend fun getRecommendations(originalQuery: String, rawResults: List<RawSearchResult>): List<PromotionItem>
}
