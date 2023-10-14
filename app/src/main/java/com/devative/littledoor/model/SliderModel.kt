package com.devative.littledoor.model

data class SliderModel(
    val `data`: List<Data>,
    val status: Boolean
) {
    data class Data(
        val end_date: String,
        val id: Int,
        val image_url: String,
        val is_always: Int,
        val start_date: String,
        val title: String
    )
}