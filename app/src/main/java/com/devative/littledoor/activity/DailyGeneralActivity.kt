package com.devative.littledoor.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.devative.littledoor.R
import com.devative.littledoor.adapter.DailyGeneralAdapter
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DailyJournalVM
import com.devative.littledoor.databinding.ActivityDailyGeneralBinding
import com.devative.littledoor.model.DailyJournalModel
import com.devative.littledoor.model.EmotModel
import com.devative.littledoor.model.SearchAbleList
import com.devative.littledoor.util.CalendarBottomSheetDialogFragment
import com.devative.littledoor.util.DailyGeneraleBottomSheet
import com.devative.littledoor.util.Logger
import com.devative.littledoor.util.SingleSelectBottomSheetDialogFragment
import com.devative.littledoor.util.Utility
import com.devative.littledoor.verticalweekcalendar.VerticalWeekCalendar
import com.devative.littledoor.verticalweekcalendar.controller.VerticalWeekAdapter
import com.devative.littledoor.verticalweekcalendar.interfaces.DateWatcher
import com.devative.littledoor.verticalweekcalendar.model.CalendarDay
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

class DailyGeneralActivity : BaseActivity(), DailyGeneralAdapter.DailyGeneralAdapterEvent {
    private val binding: ActivityDailyGeneralBinding by lazy {
        ActivityDailyGeneralBinding.inflate(layoutInflater)
    }
    private lateinit var adapter: DailyGeneralAdapter
    private val vm: DailyJournalVM by viewModels()
    private val emoteList = ArrayList<EmotModel.Data>()
    private val dailyJournalList = ArrayList<DailyJournalModel.Data>()
    var selected: GregorianCalendar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val calendar = Calendar.getInstance()
        setUpwatcher(calendar)
        binding.txtMonth.text = Constants.monthNames[calendar[Calendar.MONTH]]
        adapter = DailyGeneralAdapter(this, this,dailyJournalList)
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
            val dialog = DailyGeneraleBottomSheet(emoteList,-1,object :
                DailyGeneraleBottomSheet.DailyGeneraleBottomSheetEvent {
                override fun onSubmit(id: Int, message: String) {
                    val year = selected?.get(Calendar.YEAR)
                    val month = selected?.get(Calendar.MONTH)?.plus(1) // Adding 1 since Calendar.MONTH is 0-based
                    val day = selected?.get(Calendar.DAY_OF_MONTH)

                    val dataMap = HashMap<String, Any>()
                    dataMap["emotion_id"] = id
                    dataMap["date"] = Utility.getCurrentDateFormatted("$year-$month-$day")
                    dataMap["message"] = message
                    vm.postJournal(dataMap)
                }
            })
            dialog.show(supportFragmentManager, "ImagePickerDialog")
        }
        observe()
    }

    private fun observe() {
        vm.getJournal()
        vm.getEmote()

        vm.getEmote.observe(this) {
            when (it.status) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    if (it.data?.status == true) {
                        emoteList.clear()
                        if (it.data.data.isNotEmpty()) {
                            emoteList.addAll(it.data.data as ArrayList)
                        }
                    }
                }

                Status.ERROR -> {
                }

            }
        }
        vm.getJournal.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    if (!progress.isShowing())
                        progress.show()
                }

                Status.SUCCESS -> {
                    progress?.dismiss()
                    if (it.data?.status == true) {
                        dailyJournalList.clear()
                        if (it.data.data.isNotEmpty()) {
                            dailyJournalList.addAll(it.data.data as ArrayList)
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        Toasty.error(applicationContext, getString(R.string.some_thing_went_wrong))
                            .show()
                    }
                }

                Status.ERROR -> {
                    progress?.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }

        vm.postJournal.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress?.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true){
                        vm.getJournal()
                        Utility.successToast(applicationContext,it.data.message)
                    }
                }

                Status.ERROR -> {
                    progress?.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
        vm.deleteJournal.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress?.show()
                }

                Status.SUCCESS -> {
                    if (it.data?.status == true){
                        vm.getJournal()
                        Utility.successToast(applicationContext,it.data.message)
                    }else{
                        it.data?.message?.let { it1 -> Utility.errorToast(applicationContext, it1) }
                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }


    }

    private fun setUpwatcher(calendar: Calendar) {

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

    override fun onMore(position: DailyJournalModel.Data) {
        val items = arrayListOf(SearchAbleList(0,"Delete",R.drawable.delete))
        val dialog = SingleSelectBottomSheetDialogFragment(
            this,
            items,
            "Menu Option",
            cancelAble = true
        ) { selectedValue ->
            when(selectedValue.position){
               0 -> vm.deleteJournal(position.id)
            }
        }
        dialog.show(supportFragmentManager,"SearchAbleList")
    }


}