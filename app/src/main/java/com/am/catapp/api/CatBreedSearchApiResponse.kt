package com.am.catapp.api

data class CatBreedSearchApiResponse(
    val id: String,
    val name: String,
    val alt_names: String,
    val temperament: String,
    val description: String,
    val wikipedia_url: String,
    val image: ImageResult
)

data class ImageResult(
    val id: String,
    val url: String
)


