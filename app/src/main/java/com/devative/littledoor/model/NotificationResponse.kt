package com.devative.littledoor.model

data class NotificationResponse(
    val `data`: List<Data>,
    val status: Boolean
) {
    data class Data(
        val id: Int,
        val message: String,
        val notification_type: String,
        val user_id: Int
    )
}