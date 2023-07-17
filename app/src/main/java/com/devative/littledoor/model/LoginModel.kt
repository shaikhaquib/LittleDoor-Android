package com.devative.littledoor.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey

data class LoginModel(
    val api_token: String,
    val user_details: BasicDetails,
    val message: String,
    val status: Boolean
) {
    data class BasicDetails(
        val id: Int=0,
        val city_id: Int,
        val dob: String,
        val email: String,
        val gender: String,
        val name: String
    )
}