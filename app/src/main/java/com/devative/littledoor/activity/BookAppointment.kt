package com.devative.littledoor.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.devative.littledoor.R
import com.devative.littledoor.adapter.DoctorDetailsAdapter
import com.devative.littledoor.adapter.TimeSlotAdapter
import com.devative.littledoor.adapter.TimeSlotAdapterByDate
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityBookAppointmentBinding
import com.devative.littledoor.model.AvailableSlotModel
import com.devative.littledoor.model.DoctotorListRes
import com.devative.littledoor.util.Utility
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.HashMap
import java.util.Locale

class BookAppointment : BaseActivity() {
    val binding: ActivityBookAppointmentBinding by lazy {
        ActivityBookAppointmentBinding.inflate(layoutInflater)
    }
    val thDetails: DoctotorListRes.Data by lazy {
        intent.getSerializableExtra(Constants.TH_DETAILS) as DoctotorListRes.Data
    }
    val slotList = ArrayList<AvailableSlotModel.Data>()
    private val vm:MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setData()
        val currentDate = Calendar.getInstance().time
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate)
        val dayId = getDayID(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
        getAvaibaleSLot(dayId, formattedDate)
        setupCalendar()
        setUPObserver()
    }

    private fun setUPObserver() {
        vm.availableSlots.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        if (!it.data.data.isNullOrEmpty()) {
                            slotList.clear()
                            slotList.addAll(it.data.data as ArrayList)
                            binding.rvTimeSlot.adapter = TimeSlotAdapterByDate(this,slotList)
                            binding.txtError.visibility = View.GONE
                            binding.rvTimeSlot.visibility = View.VISIBLE
                        }else{
                            binding.txtError.text = "No Slots Available for the day"
                            binding.txtError.visibility = View.VISIBLE
                            binding.rvTimeSlot.visibility = View.GONE
                        }
                    }else{
                        binding.txtError.text = "No Slots Available for the day"
                        binding.txtError.visibility = View.VISIBLE
                        binding.rvTimeSlot.visibility = View.GONE
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

    private fun setupCalendar() {
        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            val selectedDayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK)
            val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            val dayId = getDayID(selectedDayOfWeek)
            getAvaibaleSLot(dayId, formattedDate)

        }
    }

    private fun getAvaibaleSLot(dayId: Int, formattedDate: String) {
        val hashMap = HashMap<String, Any>()
        hashMap["doctor_id"] = thDetails.id
        hashMap["day_id"] = dayId
        hashMap["date"] = formattedDate
        vm.getAvailableSLotByDate(hashMap)
    }

    private fun getDayID(selectedDayOfWeek: Int): Int {
        val dayId = when (selectedDayOfWeek) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1 // Invalid day
        }
        return dayId
    }

    private fun setData() {
        thDetails.apply {
            binding.apply {
                txtName.text = "$name - $category_name"
                txtCharges.text = "$doctor_session_charge"
            }
        }
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


}