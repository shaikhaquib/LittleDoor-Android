package com.devative.littledoor.model

data class DRStatsModel(
    val `data`: Data,
    val status: Boolean
) {
    data class Data(
        val last_fifteen_day_wise: List<LastFifteenDayWise>,
        val last_seven_day_wise: List<LastSevenDayWise>,
        val month_wise: List<MonthWise>
    ) {
        data class LastFifteenDayWise(
            val appointment_count: Int,
            val week: String
        )

        data class LastSevenDayWise(
            val appointment_count: Int,
            val day: String
        )

        data class MonthWise(
            val appointment_count: Int,
            val month: String
        )
    }
}