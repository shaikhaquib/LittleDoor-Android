package com.devative.littledoor.verticalweekcalendar.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.verticalweekcalendar.controller.VerticalWeekAdapter.DayViewHolder
import com.devative.littledoor.verticalweekcalendar.interfaces.DateClickCallback
import com.devative.littledoor.verticalweekcalendar.interfaces.DateWatcher
import com.devative.littledoor.verticalweekcalendar.interfaces.OnDateClickListener
import com.devative.littledoor.verticalweekcalendar.interfaces.ResProvider
import com.devative.littledoor.verticalweekcalendar.model.CalendarDay
import java.util.Calendar
import java.util.Collections
import java.util.GregorianCalendar

class VerticalWeekAdapter(private val resProvider: ResProvider, private val now: Calendar) :
    RecyclerView.Adapter<DayViewHolder>(), DateClickCallback {
    @JvmField
    var days: MutableList<CalendarDay?> = ArrayList()
    private var recyclerView: RecyclerView? = null
    private var dateWatcher: DateWatcher? = null
    private var onDateClickListener: OnDateClickListener? = null

    init {
        initCalendar()
    }

    private fun initCalendar() {
        val createdDays: MutableList<CalendarDay?> = ArrayList()
        for (i in 0..15) {
            val today: Calendar = GregorianCalendar(
                now[Calendar.YEAR],
                now[Calendar.MONTH],
                now[Calendar.DAY_OF_MONTH]
            )
            today.add(Calendar.DAY_OF_MONTH, i * -1)
            val createdDay = CalendarDay(
                today[Calendar.YEAR],
                today[Calendar.MONTH],
                today[Calendar.DAY_OF_MONTH]
            )
            createdDays.add(createdDay)
        }
        Collections.reverse(createdDays)
        days.addAll(createdDays)
        for (i in 1..15) {
            val today: Calendar = GregorianCalendar(
                now[Calendar.YEAR],
                now[Calendar.MONTH],
                now[Calendar.DAY_OF_MONTH]
            )
            today.add(Calendar.DAY_OF_MONTH, i)
            val createdDay = CalendarDay(
                today[Calendar.YEAR],
                today[Calendar.MONTH],
                today[Calendar.DAY_OF_MONTH]
            )
            days.add(createdDay)
        }
    }

    fun addCalendarDays(loadAfter: Boolean) {
        val insertIdx = if (loadAfter) days.size - 1 else 0
        val insertionPoint = days[insertIdx]
        val createdDays: MutableList<CalendarDay?> = ArrayList()
        for (i in 1..10) {
            val startDay: Calendar = GregorianCalendar(
                insertionPoint!!.year,
                insertionPoint.month,
                insertionPoint.day[Calendar.DAY_OF_MONTH]
            )
            val daysToAppendOrPrepend = if (loadAfter) i else i * -1
            startDay.add(Calendar.DAY_OF_MONTH, daysToAppendOrPrepend)
            val newDay = CalendarDay(
                startDay[Calendar.YEAR],
                startDay[Calendar.MONTH],
                startDay[Calendar.DAY_OF_MONTH]
            )
            createdDays.add(newDay)
        }
        if (!loadAfter) Collections.reverse(createdDays)
        days.addAll(if (loadAfter) insertIdx + 1 else 0, createdDays)
        notifyItemRangeInserted(if (loadAfter) insertIdx + 1 else 0, 10)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        return DayViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.verticalweekcalendar_day, parent, false),
            resProvider,
            this
        )
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        day!!.state = dateWatcher!!.getStateForDate(
            day.year,
            day.month,
            day.day[Calendar.DAY_OF_MONTH],
            holder
        )
        holder.display(day)
    }

    override fun getItemCount(): Int {
        return days.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    fun setOnDateClickListener(onDateClickListener: OnDateClickListener?) {
        this.onDateClickListener = onDateClickListener
    }

    fun setDateWatcher(dateWatcher: DateWatcher?) {
        this.dateWatcher = dateWatcher
    }

    override fun onCalenderDayClicked(year: Int, month: Int, day: Int) {
        if (onDateClickListener != null) onDateClickListener!!.onCalenderDayClicked(
            year,
            month,
            day
        )
    }

    inner class DayViewHolder internal constructor(
        itemView: View,
        private val resProvider: ResProvider,
        private val clickCallback: DateClickCallback
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val intToMonth = arrayOf(
            "JAN",
            "FEB",
            "MAR",
            "APR",
            "MAY",
            "JUN",
            "JUL",
            "AUG",
            "SEP",
            "OCT",
            "NOV",
            "DEC"
        )
        private val intToWeekday = arrayOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
        private var currentDay: CalendarDay? = null
        var dayView: LinearLayout
        var dayOfWeek: TextView
        var dayOfMonth: TextView
        var month: TextView

        init {
            dayView = itemView.findViewById(R.id.container)
            dayOfWeek = itemView.findViewById(R.id.dayOfWeekText)
            dayOfMonth = itemView.findViewById(R.id.dayOfMonthText)
            month = itemView.findViewById(R.id.MonthText)
            itemView.setOnClickListener(this)
        }

        fun display(day: CalendarDay?) {
            currentDay = day
            dayView.invalidate()
            setupData(day)
            setupStyles(day)
        }

        private fun setupStyles(day: CalendarDay?) {
            when (day!!.state) {
                CalendarDay.DEFAULT -> {
                    dayView.background =
                        ContextCompat.getDrawable(dayView.context, resProvider.dayBackground)
                    dayOfMonth.setTextColor(resProvider.dayTextColor)
                    dayOfWeek.setTextColor(
                        ContextCompat.getColor(
                            month.context,
                            R.color.grey_primary
                        )
                    )
                    month.setTextColor(ContextCompat.getColor(month.context, R.color.grey_light))
                }

                CalendarDay.SELECTED -> {
                    dayView.background = ContextCompat.getDrawable(
                        dayView.context,
                        resProvider.selectedDayBackground
                    )
                    dayOfMonth.setTextColor(resProvider.selectedDayTextColor)
                    dayOfWeek.setTextColor(ContextCompat.getColor(month.context, R.color.black))
                    month.setTextColor(ContextCompat.getColor(month.context, R.color.grey_primary))
                }
            }
        }

        private fun setupData(day: CalendarDay?) {
            dayOfWeek.text = intToWeekday[day!!.day[Calendar.DAY_OF_WEEK] - 1]
            dayOfMonth.text = day.day[Calendar.DAY_OF_MONTH].toString()
            month.text = intToMonth[day.day[Calendar.MONTH]]
        }

        override fun onClick(view: View) {
            val year = currentDay!!.year
            val month = currentDay!!.month
            val day = currentDay!!.day[Calendar.DAY_OF_MONTH]
            clickCallback.onCalenderDayClicked(year, month, day)
            dateWatcher!!.onDateSelection(year, month, day)
            notifyDataSetChanged()
        }
    }
}