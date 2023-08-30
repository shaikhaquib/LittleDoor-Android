package com.devative.littledoor.model

data class AvailableSlotModel(
    val `data`: List<Data>,
    val status: Boolean
) {
    data class Data(
        val id: Int,
        var is_booked: Int,
        val slot_time: String
    )
}