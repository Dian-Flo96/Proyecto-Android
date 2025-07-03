package com.example.proyecto.data.repository


interface SearchServiceRepository {
    suspend fun fetchRawResults(query: String): List<RawSearchResult>
}
