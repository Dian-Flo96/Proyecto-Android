package com.example.proyecto.ui

import com.example.proyecto.data.model.PromotionItem
import com.example.proyecto.data.repository.RawSearchResult
import com.example.proyecto.data.repository.GeminiRecommendationServiceRepository
import com.example.proyecto.data.repository.SearchServiceRepository
import kotlinx.coroutines.delay

class MockSearchServiceRepository : SearchServiceRepository {
    override suspend fun fetchRawResults(query: String): List<RawSearchResult> {
        delay(1000) // Simulate network
        return when {
            query.equals("empty search", ignoreCase = true) -> emptyList()
            query.contains("error_search_api", ignoreCase = true) -> throw Exception("Simulated Search API error")
            else -> listOf(
                RawSearchResult("Demo Product Alpha - based on '$query'", "https://example.com/alpha", "Alpha is a great budget item with many features."),
                RawSearchResult("Demo Service Beta - for '$query'", "https://example.com/beta", "Beta offers premium support for what you searched."),
                RawSearchResult("Demo Gadget Gamma - related to '$query'", "https://example.com/gamma", "Gamma is the latest tech for your query.")
            )
        }
    }
}

class MockGeminiRecommendationServiceRepository : GeminiRecommendationServiceRepository {
    override suspend fun getRecommendations(originalQuery: String, rawResults: List<RawSearchResult>): List<PromotionItem> {
        delay(1500) // Simulate network and AI processing
        return when {
            originalQuery.contains("error_gemini_api", ignoreCase = true) -> throw Exception("Simulated Gemini API error")
            rawResults.isEmpty() -> emptyList()
            else -> rawResults.mapIndexedNotNull { index, rawResult ->
                // Simple transformation for demo, e.g., only recommend first two
                if (index < 2) {
                    PromotionItem(
                        id = "gemini-mock-${rawResult.link.hashCode()}-$index",
                        name = "Gemini Rec: ${rawResult.title.replace("Demo", "Recommended")}",
                        description = "Gemini AI analyzed: '${rawResult.snippet}' and suggests this for '$originalQuery'. This is a top pick!",
                        price = "S/ ${100 + index * 50}.00 - ${150 + index * 50}.00",
                        source = "Verified Sources (via Demo AI)",
                        webUrl = rawResult.link,
                        imageUrl = if (index == 0) "https://via.placeholder.com/150/FF0000/FFFFFF?Text=Alpha" else "https://via.placeholder.com/150/00FF00/FFFFFF?Text=Beta" // Example image
                    )
                } else null
            }
        }
    }
}
