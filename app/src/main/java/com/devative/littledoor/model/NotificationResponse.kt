package com.devative.littledoor.model

data class NotificationResponse(
    val `data`: List<Data>,
    val status: Boolean
) {
    data class Data(
        val id: Int,
        val message: String,
        val title: String,
        val user_id: Int
    )
}