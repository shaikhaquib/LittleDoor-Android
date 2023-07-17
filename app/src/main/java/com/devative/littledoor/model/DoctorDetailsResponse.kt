package com.devative.littledoor.model

import java.io.Serializable

data class DoctorDetailsResponse(
    val `data`: Data,
    val status: Boolean
) {
    data class Data(
        val address: List<Addres>,
        val address_proof_url: String,
        val appreciation: List<Appreciation>,
        val dob: String,
        val education: List<Education>,
        val first_name: String,
        val form_status: Int,
        val gender: String,
        val id: Int,
        val languages: List<String>,
        val mobile_no: String,
        val other: List<Other>,
        val skills: List<Skill>,
        val status: Int,
        val work_experience: List<WorkExperience>
    ):Serializable {
        data class Addres(
            val address_line_1: String,
            val address_line_2: String,
            val address_type: String,
            val city_id: Int,
            val city_name: String,
            val doctor_id: Int,
            val id: Int,
            val pincode: String,
            val state_id: Int,
            val state_name: String
        ):Serializable

        data class Appreciation(
            val category_achieved: String,
            val description: Any,
            val doctor_id: Int,
            val id: Int,
            val image_url: String,
            val issue_date: String,
            val name: String
        ):Serializable

        data class Education(
            val description: String,
            val doctor_id: Int,
            val documents: List<String>,
            val end_date: String,
            val field_of_study: String,
            val id: Int,
            val institution_name: String,
            val name: String,
            val start_date: String
        ):Serializable

        data class Other(
            val doctor_id: Int,
            val document: String,
            val id: Int,
            val name: String
        ):Serializable

        data class Skill(
            val doctor_id: Int,
            val id: Int,
            val skill_name: String
        ):Serializable

        data class WorkExperience(
            val category_id: Int,
            val category_name: String,
            val certificate: List<String>,
            val doctor_id: Int,
            val id: Int,
            val sub_category: List<SubCategory>
        ):Serializable {
            data class SubCategory(
                val id: Int,
                val name: String
            ):Serializable
        }
    }
}