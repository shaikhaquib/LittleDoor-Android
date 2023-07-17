package com.devative.littledoor.model

data class SubCategoryResponse(
    val `data`: List<Data>,
    val status: Boolean
) {
    data class Data(
        val id: Int,
        val master_category_id: Int,
        val master_category_name: String,
        val name: String,
        val status: Int
    )
}