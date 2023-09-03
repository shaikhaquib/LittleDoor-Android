package com.devative.littledoor.util

/**
 * Created by AQUIB RASHID SHAIKH on 04-09-2023.
 */
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.CalendarView

class CustomCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CalendarView(context, attrs, defStyleAttr) {

    init {
        setOnDateChangeListener { _, year, month, dayOfMonth ->
            val currentDate = System.currentTimeMillis()
            val selectedDate = getSelectedDate(year, month, dayOfMonth)

            if (selectedDate < currentDate) {
                // Set the color for past dates
                setDateTextAppearance(Color.GRAY)
            } else {
                // Set the color for future dates
                setDateTextAppearance(Color.BLACK)
            }
        }
    }

    private fun getSelectedDate(year: Int, month: Int, dayOfMonth: Int): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        return calendar.timeInMillis
    }
}
