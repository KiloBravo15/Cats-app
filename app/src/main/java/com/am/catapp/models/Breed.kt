package com.am.catapp.models

import java.io.Serializable

data class Breed(
    val id: String,
    val name: String,
    val altNames: String,
    val temperament: String,
    val description: String,
    val wikiUrl: String,
    val imageUrl: String?
) : Serializable {
    constructor() : this("", "", "", "", "", "", null)
}
