package com.devative.littledoor.model

data class GeneralResponse(
    val message: String,
    val otp: String,
    val status: Boolean
)