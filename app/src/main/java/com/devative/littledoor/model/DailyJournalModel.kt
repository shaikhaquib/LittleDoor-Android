package com.devative.littledoor.model

data class DailyJournalModel(
    val `data`: List<Data>,
    val status: Boolean
) {
    data class Data(
        val created_at: String,
        val doctor_id: Any,
        val emotion_id: Int,
        val emotion_name: String,
        val id: Int,
        val journal_date: String,
        val message: String,
        val patient_id: Int
    )
}