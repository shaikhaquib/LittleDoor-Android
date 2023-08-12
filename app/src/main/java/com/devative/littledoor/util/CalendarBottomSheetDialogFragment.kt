package com.devative.littledoor.util

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import com.devative.littledoor.R
import com.devative.littledoor.verticalweekcalendar.model.CalendarDay
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.Month
import java.time.Year
import java.util.Calendar


/**
 * Created by AQUIB RASHID SHAIKH on 12-08-2023.
 */
class CalendarBottomSheetDialogFragment(private var dateSelectionListener: DateSelectionListener) :
    BottomSheetDialogFragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var btnOkay: Button

    private var selectedDate: CalendarDay? = null
    private var dateString = ""
    private var day: Int = 0
    private var month: Int = 0
    private var year: Int = 0

    interface DateSelectionListener {
        fun onDateChange(day: Int, month: Int, year: Int)
        fun onDateSelected(day: Int, month: Int, year: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        calendarView = view.findViewById(R.id.calendarView)
        btnOkay = view.findViewById(R.id.btnOkay)
        val calendar = Calendar.getInstance()
        btnOkay.setText("Select (${calendar[Calendar.DAY_OF_MONTH]}/${calendar[Calendar.MONTH] + 1}/${calendar[Calendar.YEAR]})")
        day = calendar[Calendar.DAY_OF_MONTH]
        month = calendar[Calendar.MONTH]
        year = calendar[Calendar.YEAR]
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            this.day = dayOfMonth
            this.year = year
            this.month = month
            dateSelectionListener?.onDateChange(dayOfMonth, month, year)
            btnOkay.setText("Select ($day/${month + 1}/$year)")
        }

        btnOkay.setOnClickListener {
            dateSelectionListener?.onDateSelected(day, month, year)
            dismiss()
        }
    }

    fun setDateSelectionListener(listener: DateSelectionListener) {
        dateSelectionListener = listener
    }

    fun setSelectedDate(date: CalendarDay) {
        selectedDate = date
    }

}
