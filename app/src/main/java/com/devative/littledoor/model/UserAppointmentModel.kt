package com.devative.littledoor.model

data class UserAppointmentModel(
    val `data`: List<Data>,
    val status: Boolean
) {
    data class Data(
        val apointmnet_date: String,
        val doctor_id: Int,
        val doctor_name: String,
        val id: Int,
        val is_appointment_active: Int,
        val slot_id: Int,
        val slot_time: String
    )
}