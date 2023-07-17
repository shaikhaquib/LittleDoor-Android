package com.devative.littledoor.model

data class CategoryResponse(
    val `data`: List<Data>,
    val status: Boolean
) {
    data class Data(
        val id: Int,
        val image_url: String,
        val name: String
    )
}