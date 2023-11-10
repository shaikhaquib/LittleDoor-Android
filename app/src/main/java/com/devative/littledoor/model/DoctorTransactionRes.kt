package com.devative.littledoor.model

data class DoctorTransactionRes(
    val `data`: List<Data>,
    val status: Boolean
) {
    data class Data(
        val amount: Int,
        val created_at: String,
        val doctor_id: Int,
        val doctor_image: String,
        val doctor_name: String,
        val id: Int,
        val patient_id: Int,
        val patient_image: String,
        val patient_name: String,
        val status: String,
        val transaction_number: String
    )
}