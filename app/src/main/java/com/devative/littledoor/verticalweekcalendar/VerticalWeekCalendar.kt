package com.devative.littledoor.verticalweekcalendar

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.verticalweekcalendar.controller.VerticalWeekAdapter
import com.devative.littledoor.verticalweekcalendar.interfaces.DateWatcher
import com.devative.littledoor.verticalweekcalendar.interfaces.OnDateClickListener
import com.devative.littledoor.verticalweekcalendar.interfaces.ResProvider
import java.util.Calendar

class VerticalWeekCalendar : LinearLayoutCompat, ResProvider {
    private var context: Context? = null
    private var customFont: String? = null
    override var dayTextColor = 0
        private set
    override var weekDayTextColor = 0
        private set
    override var dayBackground = 0
        private set
    override var selectedDayTextColor = 0
        private set
    override var selectedDayBackground = 0
        private set
    private var adapter: VerticalWeekAdapter? = null
    private lateinit var recyclerView: RecyclerView

    constructor(context: Context?) : super(context!!) {
        this.context = context
        initLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initLayout(context)
        loadStyle(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initLayout(context)
        loadStyle(context, attrs)
    }

    private fun init() {
        setupRecyclerView()
    }

    private fun initLayout(context: Context?) {
        this.context = context
        orientation = VERTICAL
        inflate(context, R.layout.verticalweekcalendar_week, this)
    }

    private fun loadStyle(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(
            attrs,
            R.styleable.VerticalWeekCalendar,
            R.attr.verticalWeekCalendarStyleAttr,
            R.style.VerticalWeekCalendarStyle
        )
        customFont = ta.getString(R.styleable.VerticalWeekCalendar_customFont)
        dayBackground = ta.getResourceId(R.styleable.VerticalWeekCalendar_dayBackground, 0)
        dayTextColor = ta.getColor(R.styleable.VerticalWeekCalendar_dayTextColor, 0)
        weekDayTextColor = ta.getColor(R.styleable.VerticalWeekCalendar_weekDayTextColor, 0)
        selectedDayBackground =
            ta.getResourceId(R.styleable.VerticalWeekCalendar_selectedBackground, 0)
        selectedDayTextColor = ta.getColor(R.styleable.VerticalWeekCalendar_selectedDayTextColor, 0)
        ta.recycle()
    }

    fun setDate(calendar: Calendar?) {
        adapter = VerticalWeekAdapter(this, calendar!!)
        recyclerView.layoutManager =
            LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
        recyclerView.scrollToPosition(15)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var mLastFirstVisibleItem = 0
            private var mIsScrollingUp = false
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lm = recyclerView.layoutManager as LinearLayoutManager?
                val currentFirstVisibleItem = lm!!.findFirstVisibleItemPosition()
                if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                    mIsScrollingUp = false
                } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                    mIsScrollingUp = true
                }
                mLastFirstVisibleItem = currentFirstVisibleItem
                if (lm.findFirstVisibleItemPosition() < 10 && mIsScrollingUp) {
                    getAdapter().addCalendarDays(false)
                } else if (getAdapter().days.size - 1 - lm.findLastVisibleItemPosition() < 10 && !mIsScrollingUp) {
                    getAdapter().addCalendarDays(true)
                }
            }
        })
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.setLayoutManager(
            LinearLayoutManager(
                getContext(),
                RecyclerView.VERTICAL,
                false
            )
        )
        recyclerView.setAdapter(getAdapter())
        recyclerView.scrollToPosition(15)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var mLastFirstVisibleItem = 0
            private var mIsScrollingUp = false
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lm = recyclerView.layoutManager as LinearLayoutManager?
                val currentFirstVisibleItem = lm!!.findFirstVisibleItemPosition()
                if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                    mIsScrollingUp = false
                } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                    mIsScrollingUp = true
                }
                mLastFirstVisibleItem = currentFirstVisibleItem
                if (lm.findFirstVisibleItemPosition() < 10 && mIsScrollingUp) {
                    getAdapter().addCalendarDays(false)
                    Log.i(
                        "onScrollChange",
                        "FirstVisibleItem: " + lm.findFirstVisibleItemPosition()
                    )
                    Log.i("onScrollChange", "new Size: " + getAdapter().days.size)
                } else if (getAdapter().days.size - 1 - lm.findLastVisibleItemPosition() < 10 && !mIsScrollingUp) {
                    getAdapter().addCalendarDays(true)
                    Log.i("onScrollChange", "LastVisibleItem: " + lm.findLastVisibleItemPosition())
                    Log.i("onScrollChange", "new Size: " + getAdapter().days.size)
                }
            }
        })
    }

    fun getAdapter(): VerticalWeekAdapter {
        return if (adapter == null) createAdapter() else adapter!!
    }

    private fun createAdapter(): VerticalWeekAdapter {
        adapter = VerticalWeekAdapter(this, Calendar.getInstance())
        return adapter!!
    }

    fun setOnDateClickListener(callback: OnDateClickListener) {
        getAdapter().setOnDateClickListener(callback)
    }

    fun setDateWatcher(dateWatcher: DateWatcher?) {
        getAdapter().setDateWatcher(dateWatcher)
    }

    fun setCustomFont(customFont: String?) {
        this.customFont = customFont
    }

    fun setDefaultDayTextColor(defaultDayTextColor: Int) {
        dayTextColor = defaultDayTextColor
    }

    fun setDefaultBackground(defaultBackground: Int) {
        dayBackground = defaultBackground
    }

    fun setSelectedTextColor(selectedTextColor: Int) {
        selectedDayTextColor = selectedTextColor
    }

    fun setSelectedBackground(selectedBackground: Int) {
        selectedDayBackground = selectedBackground
    }

    /*
    @Override
    public Typeface getCustomFont() {
        if (customFont == null) {
            return null;
        }
        try {
            return ResourcesCompat.getFont(context, getResources().getIdentifier(customFont.split("\\.")[0],
                    "font", context.getPackageName()));
        } catch (Exception e) {
            return null;
        }
    }
*/
    class Builder {
        private var view = 0
        fun setView(view: Int): Builder {
            this.view = view
            return this
        }

        fun init(appCompatActivity: AppCompatActivity): VerticalWeekCalendar {
            val calendar = appCompatActivity.findViewById<VerticalWeekCalendar>(view)
            calendar.init()
            return calendar
        }
    }
}