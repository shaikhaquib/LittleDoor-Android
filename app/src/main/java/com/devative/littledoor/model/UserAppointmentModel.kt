package com.devative.littledoor.model

data class UserAppointmentModel(
    val `data`: List<Data>,
    val status: Boolean
) {
    data class Data(
        val id: Int,
        val apointmnet_date: String,
        val doctor_id: Int,
        val doctor_name: String,
        val doctor_profile: String,
        val doctor_category: List<String>,
        val is_appointment_active: Int,
        val slot_id: Int,
        val slot_time: String,
        val patient_name: String,
        val patient_profile: String?,
    )
}
