package com.devative.littledoor.model

import java.io.Serializable

data class DoctotorListRes(
    val `data`: List<Data>,
    val status: Boolean
):Serializable {
    data class Data(
        val appreciation: List<DoctorDetailsResponse.Data.Appreciation>,
        val category_name: String,
        val city: String,
        val email: String,
        val id: Int,
        val image: String?,
        val name: String,
        val skills: List<DoctorDetailsResponse.Data.Skill>,
        val languages: List<String>,
        val state: String,
        val time_slot: List<Any>,
        val total_year_of_experience: Int = 1,
        val doctor_session_charge: String = "300"
    ):Serializable
}