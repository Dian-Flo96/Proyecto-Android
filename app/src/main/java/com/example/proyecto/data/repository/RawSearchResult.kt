package com.example.proyecto.data.repository

data class RawSearchResult(
    val title: String,
    val link: String,
    val snippet: String? // A short description from the search result
    // Add other fields if your chosen Search API provides them, e.g., displayLink
)