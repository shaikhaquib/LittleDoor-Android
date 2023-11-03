package com.devative.littledoor.model

import java.io.Serializable

data class ChatListResponse(
    val `data`: List<Data>,
    val status: Boolean
):Serializable {
    data class Data(
        val chat_id: Int,
        val created_at: String,
        val doctor_id: Int,
        val doctor_image_url: String?,
        val doctor_name: String,
        val patient_id: Int,
        val patient_image_url: String?,
        val patient_name: String
    ):Serializable
}