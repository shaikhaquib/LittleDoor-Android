package com.devative.littledoor.model

data class TimeSLotModel(
    val `data`: List<Data>,
    val status: Boolean
) {
    data class Data(
        val id: Int,
        val slot_time: String,
        var isSelected:Boolean = false
    )
}