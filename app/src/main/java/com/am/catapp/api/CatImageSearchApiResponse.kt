package com.am.catapp.api

data class CatImageSearchApiResponse(
    val id: String,
    val url: String,
    val breeds: List<BreedResult>
)

data class BreedResult(
    val id: String,
    val name: String,
    val alt_names: String,
    val temperament: String,
    val description: String,
    val wikipedia_url: String
)