package com.devative.littledoor.model

data class DoctotorListRes(
    val `data`: List<Data>,
    val status: Boolean
) {
    data class Data(
        val category_name: String,
        val city: String,
        val email: String,
        val id: Int,
        val image: String?,
        val name: String,
        val state: String,
        val total_year_of_experience: Int
    )
}