package com.devative.littledoor.verticalweekcalendar.interfaces;


import com.devative.littledoor.verticalweekcalendar.controller.VerticalWeekAdapter;

public interface DateWatcher {
    int getStateForDate(int year, int month, int day, VerticalWeekAdapter.DayViewHolder view);
    void onDateSelection(int year, int month, int day);
}
