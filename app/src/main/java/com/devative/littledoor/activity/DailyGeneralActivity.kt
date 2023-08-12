package com.devative.littledoor.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.devative.littledoor.R
import com.devative.littledoor.adapter.DailyGeneralAdapter
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.databinding.ActivityDailyGeneralBinding
import com.devative.littledoor.util.CalendarBottomSheetDialogFragment
import com.devative.littledoor.util.DailyGeneraleBottomSheet
import com.devative.littledoor.util.IOSDatePickerFragment
import com.devative.littledoor.util.Logger
import com.devative.littledoor.verticalweekcalendar.VerticalWeekCalendar
import com.devative.littledoor.verticalweekcalendar.controller.VerticalWeekAdapter
import com.devative.littledoor.verticalweekcalendar.interfaces.DateWatcher
import com.devative.littledoor.verticalweekcalendar.interfaces.OnDateClickListener
import com.devative.littledoor.verticalweekcalendar.model.CalendarDay
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Objects

class DailyGeneralActivity : AppCompatActivity(),DailyGeneralAdapter.DailyGeneralAdapterEvent {
    private val binding: ActivityDailyGeneralBinding by lazy {
        ActivityDailyGeneralBinding.inflate(layoutInflater)
    }
    private lateinit var adapter:DailyGeneralAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val calendar = Calendar.getInstance()
        setUpwatcher(calendar)
        binding.txtMonth.text = Constants.monthNames[calendar[Calendar.MONTH]]
        adapter = DailyGeneralAdapter(this,this)
        binding.rvGeneral.adapter = adapter
        adapter.notifyDataSetChanged()

//        binding.txtMonth.setText(calendarView.g)
        binding.txtMonth.setOnClickListener {
            val bottomSheetFragment = CalendarBottomSheetDialogFragment((object :
                CalendarBottomSheetDialogFragment.DateSelectionListener {
                override fun onDateChange(day: Int, month: Int, year: Int) {

                }

                override fun onDateSelected(day: Int, month: Int, year: Int) {
                    if (month != 0) {
                        binding.verticalCalendar.invalidate()
                        val calendar = Calendar.getInstance()
                        calendar.set(year, month, day)
                        setUpwatcher(calendar)
                        binding.txtMonth.text = Constants.monthNames[month]
                    }
                }
            }))
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)


        }

        binding.addDailyGenral.setOnClickListener {
            val dialog = DailyGeneraleBottomSheet(
                this
            )
            dialog.show(supportFragmentManager, "ImagePickerDialog")
        }


    }

    private fun setUpwatcher(calendar: Calendar) {
        var selected: GregorianCalendar? = null
        var calendarView: VerticalWeekCalendar? = null
        selected = GregorianCalendar(
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        )

        calendarView = VerticalWeekCalendar.Builder()
            .setView(binding.verticalCalendar.id)
            .init(this)

        calendarView.setDate(calendar)

        calendarView?.setOnDateClickListener { year, month, day ->
            val selectedDay = GregorianCalendar(year, month, day)
            if (selected?.compareTo(selectedDay) != 0) {
                selected = selectedDay
            }
        }
        calendarView?.setDateWatcher(object : DateWatcher {
            override fun getStateForDate(
                year: Int,
                month: Int,
                day: Int,
                view: VerticalWeekAdapter.DayViewHolder?
            ): Int {
                return if (selected?.compareTo(
                        GregorianCalendar(
                            year,
                            month,
                            day
                        )
                    ) == 0
                ) CalendarDay.SELECTED
                else
                    CalendarDay.DEFAULT
            }

            override fun onDateSelection(year: Int, month: Int, day: Int) {
                Toast.makeText(applicationContext, "$day/$month/$year", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onclick(position: Int) {

    }


}