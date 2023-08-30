package com.devative.littledoor.model

data class SessionDetails(
    val `data`: Data,
    val status: Boolean
) {
    data class Data(
        val consultancy_status: Int,
        val session_charge: String,
        val slot_time: List<SlotTime>
    ) {
        data class SlotTime(
            val day: String,
            val days_id: Int,
            var slots: List<Slot>
        ) {
            data class Slot(
                val slot_id: Int,
                val slot_time: String
            )
        }
    }
}