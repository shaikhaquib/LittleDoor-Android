package com.devative.littledoor.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import com.devative.littledoor.R
import com.devative.littledoor.adapter.THCreateSlotAdapter
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.architecturalComponents.viewmodel.UpdateSessionDetailsVM
import com.devative.littledoor.databinding.ActivityThaddSessionDetailsBinding
import com.devative.littledoor.model.TimeSLotModel
import com.devative.littledoor.util.AllTimeSlotSelectDialog
import com.devative.littledoor.util.ImagePickerDialog
import com.devative.littledoor.util.Utility
import es.dmoral.toasty.Toasty
import java.sql.Time

class THAddSessionDetailsActivity : BaseActivity() {
    val binding: ActivityThaddSessionDetailsBinding by lazy {
        ActivityThaddSessionDetailsBinding.inflate(layoutInflater)
    }
    private val vm: UpdateSessionDetailsVM by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private val timeSlotsList = ArrayList<TimeSLotModel.Data>()

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
        setUpUI()
    }

    private fun observe() {
        basicDetails?.doctor_id?.let { vm.getSessionDetails(it) }
        vm.getAllTimeSlots()
        vm.getSessionAmount.observe(this){
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    it.data?.let {session->
                        if (session.status) {
                            if (!session.data.isNullOrEmpty()) {
                                binding.edtAmount.setText(session.data[0].session_charge_amount)
                            }
                        }else{
                            Utility.errorToast(applicationContext,getString(R.string.some_thing_went_wrong))
                        }
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
    }

    private fun setUpUI() {
        binding.rvSlots.adapter = THCreateSlotAdapter(this,object :
            THCreateSlotAdapter.THCreateSlotAdapterEvent {
            override fun onclick(position: Int) {

            }

            override fun onOpenSelection(day: Int) {
                val dialog = AllTimeSlotSelectDialog(timeSlotsList)
                dialog.show(supportFragmentManager, "AllTimeSlotSelectDialog")

            }
        })
        binding.btnSUbmit.setOnClickListener {
            if (binding.edtAmount.text.isEmpty() || binding.edtAmount.text.toString().toInt() == 0){
                Utility.errorToast(applicationContext,"To continue please enter valid amount")
            }else if (binding.edtAmount.text.toString().toInt() > 5000){
                Utility.errorToast(applicationContext,"The session amount cannot be greater than Rs.5000, it should be in between Rs.1 to Rs.5000")
            }else{
                basicDetails?.doctor_id?.let { it1 -> vm.setSessionAmount(it1,binding.edtAmount.text.toString()) }
            }
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

}