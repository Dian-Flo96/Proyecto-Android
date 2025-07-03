package com.example.proyecto.data.model

data class PromotionItem(
    val id: String, // Unique ID for the item
    val name: String,
    val description: String?,
    val price: String?, // Or Double, if you can ensure consistent formatting
    val source: String?, // e.g., "Amazon", "BestBuy"
    val webUrl: String?, // Direct link to the product page
    val imageUrl: String? // Optional image for the item
)