package com.devative.littledoor.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class UserDetails(
    val `data`: Data,
    val status: Boolean
) {
    @Entity(tableName = "users")
    data class Data(
        val city_id: Int,
        val form_status: Int,
        val status: Int,
        val city_name: String?,
        val dob: String,
        val doctor_id: Int?,
        val email: String,
        val gender: String,
        @PrimaryKey(true)
        val id: Int,
        val mobile_no: String,
        val name: String,
        val image_url: String?,
        val pateint_id: Int?
    )
}