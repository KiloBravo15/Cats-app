package com.am.catapp.models

import java.io.Serializable

data class Cat(
    val id: String,
    val imageUrl: String?,
    val breed: Breed
) : Serializable {
    constructor() : this("", null, Breed("", "", "", "", "", "", null))
}