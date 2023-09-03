package com.devative.littledoor.verticalweekcalendar.model

import java.io.Serializable
import java.util.Calendar
import java.util.GregorianCalendar

class CalendarDay(val year: Int, val month: Int, day: Int) : Serializable {
    var state = DEFAULT
    val day: GregorianCalendar

    init {
        this.day = GregorianCalendar(year, month, day)
    }

    override fun toString(): String {
        return "Day: " + day[Calendar.DAY_OF_MONTH] + " State: " + state
    }

    companion object {
        const val DEFAULT = 0
        const val SELECTED = 1
        const val TODAY = 2
    }
}