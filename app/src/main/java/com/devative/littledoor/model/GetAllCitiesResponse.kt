package com.devative.littledoor.model

data class GetAllCitiesResponse(
    val `data`: List<Data>,
    val status: Boolean
) {
    data class Data(
        val city_name: String,
        val id: Int,
        val state_id: Int,
        val state_name: String
    )
}