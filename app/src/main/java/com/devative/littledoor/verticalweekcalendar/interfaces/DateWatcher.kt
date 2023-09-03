package com.devative.littledoor.verticalweekcalendar.interfaces

import com.devative.littledoor.verticalweekcalendar.controller.VerticalWeekAdapter.DayViewHolder

interface DateWatcher {
    fun getStateForDate(year: Int, month: Int, day: Int, view: DayViewHolder?): Int
    fun onDateSelection(year: Int, month: Int, day: Int)
}