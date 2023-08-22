package com.devative.littledoor.model

data class SessionDetails(
    val `data`: List<Data>,
    val status: Boolean
) {
    data class Data(
        val doctor_id: Int,
        val id: Int,
        val session_charge_amount: String
    )
}