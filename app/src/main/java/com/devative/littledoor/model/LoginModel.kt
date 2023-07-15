package com.devative.littledoor.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey

data class LoginModel(
    val api_token: String,
    val basic_details: BasicDetails,
    val message: String,
    val status: Boolean
) {
    @Entity(tableName = "users")
    data class BasicDetails(
        @PrimaryKey(true)
        val id: Int=0,
        val city_id: Int,
        val dob: String,
        val email: String,
        val gender: String,
        val name: String
    )
}