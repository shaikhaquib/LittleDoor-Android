package com.devative.littledoor.model

data class EmotModel(
    val `data`: List<Data>,
    val status: Boolean
) {
    data class Data(
        val id: Int,
        val name: String
    )
}