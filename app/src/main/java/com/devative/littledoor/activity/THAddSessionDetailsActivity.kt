package com.devative.littledoor.activity

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import com.devative.littledoor.R
import com.devative.littledoor.adapter.THCreateSlotAdapter
import com.devative.littledoor.architecturalComponents.apicall.APIClient
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DrRegViewModel
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.architecturalComponents.viewmodel.UpdateSessionDetailsVM
import com.devative.littledoor.databinding.ActivityThaddSessionDetailsBinding
import com.devative.littledoor.model.SessionDetails
import com.devative.littledoor.model.TimeSLotModel
import com.devative.littledoor.util.AllTimeSlotSelectDialog
import com.devative.littledoor.util.Utility
import es.dmoral.toasty.Toasty

class THAddSessionDetailsActivity : BaseActivity() {
    val binding: ActivityThaddSessionDetailsBinding by lazy {
        ActivityThaddSessionDetailsBinding.inflate(layoutInflater)
    }
    private val vm: UpdateSessionDetailsVM by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private val vmDR: DrRegViewModel by viewModels()

    private val timeSlotsList = ArrayList<TimeSLotModel.Data>()
    private val daysList = ArrayList<SessionDetails.Data.SlotTime>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        mainViewModel.fetchUserData()
        mainViewModel.basicDetails.observe(this){
            if (!it.isNullOrEmpty()){
                basicDetails = it[0]
                observe()
            }
        }
        setUpdays()
        setUpUI()
    }

    private fun setUpdays() {
        val weekDays: Array<String> = resources.getStringArray(R.array.week_days)
        daysList.add(SessionDetails.Data.SlotTime(weekDays[0],1, emptyList()))
        daysList.add(SessionDetails.Data.SlotTime(weekDays[1],2, emptyList()))
        daysList.add(SessionDetails.Data.SlotTime(weekDays[2],3, emptyList()))
        daysList.add(SessionDetails.Data.SlotTime(weekDays[3],4, emptyList()))
        daysList.add(SessionDetails.Data.SlotTime(weekDays[4],5, emptyList()))
        daysList.add(SessionDetails.Data.SlotTime(weekDays[5],6, emptyList()))
        daysList.add(SessionDetails.Data.SlotTime(weekDays[6],7, emptyList()))
        binding.rvSlots.adapter = THCreateSlotAdapter(this,daysList,object :
            THCreateSlotAdapter.THCreateSlotAdapterEvent {
            override fun onclick(position: Int) {

            }

            override fun onOpenSelection(day: Int) {
                val slots = ArrayList<TimeSLotModel.Data>()
                for(slot in timeSlotsList){
                    if (slot.isSelected){
                        slot.isSelected =  false
                    }
                    slots.add(slot)
                }
                if (!daysList[day].slots.isNullOrEmpty()){
                    val ids =  extractIds(daysList[day].slots)
                    for (time in slots){
                        if (ids.contains(time.id)){
                            time.isSelected = true
                        }
                    }
                }
                val dialog = AllTimeSlotSelectDialog(slots, object :
                    AllTimeSlotSelectDialog.AllTimeSlotSelectDialogEvent {
                    override fun onSubmit(selected: List<TimeSLotModel.Data>) {
                        if (selected.isNotEmpty()){
                            val list =  ArrayList<SessionDetails.Data.SlotTime.Slot>()
                            for (timeSlot in selected){
                                list.add(SessionDetails.Data.SlotTime.Slot(
                                    timeSlot.id,
                                    timeSlot.slot_time
                                ))
                            }
                            val item = daysList[day]
                            item.slots = list
                           // daysList.add(day,item)
                            binding.rvSlots.adapter?.notifyDataSetChanged()
                        }
                    }
                })
                dialog.show(supportFragmentManager, "AllTimeSlotSelectDialog")
            }

            override fun clearSelection(day: Int) {
                val item = daysList[day]
                item.slots = emptyList()
                binding.rvSlots.adapter?.notifyDataSetChanged()
            }
        })

    }

    @SuppressLint("SuspiciousIndentation")
    private fun observe() {
        basicDetails?.doctor_id?.let { vm.getSessionDetails(it) }
        vm.getAllTimeSlots()
        vm.getSessionAmount.observe(this){
            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                  it.data?.let {session->
                        if (session.status) {
                            if (session.data != null) {
                                binding.edtAmount.setText(session.data.session_charge)
                                if (!session.data.slot_time.isNullOrEmpty()) {
                                    for (i in session.data.slot_time) {
                                        if (!i.slots.isNullOrEmpty()) {
                                            val dayData = daysList[i.days_id - 1]
                                            dayData.slots = i.slots
                                            binding.rvSlots.adapter?.notifyDataSetChanged()
                                        }
                                    }
                                }
                            }
                        }else{
                            Utility.errorToast(applicationContext,getString(R.string.some_thing_went_wrong))
                        }
                    }
                }

                Status.ERROR -> {
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
        vm.setSessionAmount.observe(this){
            when (it.status) {
                Status.LOADING -> {
                progress.show()
                }

                Status.SUCCESS -> {
                 progress.dismiss()
                    it.data?.let { it1 -> Toasty.success(applicationContext, it1.message).show() }
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
        vm.setAvailability.observe(this){
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    it.data?.let { it1 -> Toasty.success(applicationContext, it1.message).show() }
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
        vm.getAllTimeSlot.observe(this){
            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    timeSlotsList.clear()
                    if (it.data != null && it.data.data.isNotEmpty()){
                        timeSlotsList.addAll(it.data.data as ArrayList<TimeSLotModel.Data>)
                    }
                }

                Status.ERROR -> {
                }

            }

        }
        vmDR.uploadResponse.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        Toasty.success(applicationContext, it.data.message).show()
                    } else {
                        Toasty.error(applicationContext, it.data!!.message).show()
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

    private fun setUpUI() {
        binding.btnSUbmit.setOnClickListener {
            if (binding.edtAmount.text.isNullOrEmpty() || binding.edtAmount.text.toString().toInt() == 0){
                Utility.errorToast(applicationContext,"To continue please enter valid amount")
            }else if (binding.edtAmount.text.toString().toInt() > 5000){
                Utility.errorToast(applicationContext,"The session amount cannot be greater than Rs.5000, it should be in between Rs.1 to Rs.5000")
            }else{
                basicDetails?.doctor_id?.let { it1 -> vm.setSessionAmount(it1,binding.edtAmount.text.toString()) }
            }
        }

        binding.btnUpdate.setOnClickListener {
            uploadTimeSlots()
        }

        binding.swOnOff.setOnCheckedChangeListener { p0, b ->
            basicDetails?.doctor_id?.let { it1 -> vm.setAvailability(it1,
                if (b) 1 else 0
            ) }
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

    private fun uploadTimeSlots() {
        if(daysList.isEmpty()){
            Toasty.error(this,"Please create some slots").show()
            return
        }
        val fileMap = HashMap<String, Uri>()
        val dataMap = HashMap<String,String>()
        dataMap["doctor_id"] = basicDetails?.doctor_id.toString()
        for (i in daysList.indices) {
            if (!daysList[i].slots.isNullOrEmpty()) {
                dataMap["time_slot[$i][day_id]"] = daysList[i].days_id.toString()
                for (j in daysList[i].slots.indices) {
                    dataMap["time_slot[$i][slot_ids][$j]"] = daysList[i].slots[j].slot_id.toString()
                }
            }
        }
        vmDR.uploadData(
            this,
            fileMap,
            dataMap,
            APIClient.DR_CREATE_TIMESLOT
        )

    }

    fun extractIds(dataList: List<SessionDetails.Data.SlotTime.Slot>): ArrayList<Int> {
        val idArray = ArrayList<Int>()
        for (i in dataList.indices) {
            idArray.add(dataList[i].slot_id)
        }
        return idArray
    }


}