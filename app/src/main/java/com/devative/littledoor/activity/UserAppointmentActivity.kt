package com.devative.littledoor.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.devative.littledoor.R
import com.devative.littledoor.adapter.AppointmentAdapter
import com.devative.littledoor.adapter.UserAppointmentListAdapter
import com.devative.littledoor.architecturalComponents.helper.Constants.filterAndSortData
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityUserAppointmentBinding
import com.devative.littledoor.model.UserAppointmentModel
import com.devative.littledoor.util.ListSpacingDecoration
import com.google.android.material.tabs.TabLayout
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

class UserAppointmentActivity : BaseActivity() {
    private val binding : ActivityUserAppointmentBinding by lazy {
        ActivityUserAppointmentBinding.inflate(layoutInflater)
    }
    private val vm:MainViewModel by viewModels()
    private val mainList = ArrayList<UserAppointmentModel.Data>()
    private val list = ArrayList<UserAppointmentModel.Data>()
    lateinit var adapter:UserAppointmentListAdapter
    private var filterCode = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.rvAppointment.addItemDecoration(ListSpacingDecoration())
        adapter = UserAppointmentListAdapter(this,list,object:
            UserAppointmentListAdapter.UserAppointmentAdapterEvent {
            override fun onclick(position: Int) {

            }
        })
        adapter.setHasStableIds(true)
        binding.rvAppointment.adapter =adapter
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val tabID = tab?.text.toString()
                if (tabID == getString(R.string.today) && filterCode != 1){
                    filterCode = 1
                    refreshList()
                } else if (tabID == getString(R.string.upcoming) && filterCode != 2){
                    filterCode = 2
                    refreshList()
                } else if (tabID ==  getString(R.string.previous) && filterCode != 3){
                    filterCode = 3
                    refreshList()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        observe()
    }

    private fun observe() {
        vm.getUserBookedAppointment()
        vm.getUserBookedAppointment.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    if (!progress.isShowing())
                        progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        mainList.clear()
                        if (it.data.data.isNotEmpty()) {
                            mainList.addAll(it.data.data as ArrayList)
                            refreshList()
                        }
                    } else {
                        Toasty.error(applicationContext, getString(R.string.some_thing_went_wrong))
                            .show()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun refreshList(){
        list.clear()
        val l =  filterAndSortData(mainList,filterCode)
        if (l.isNotEmpty()) {
            list.addAll(l)
        }
        adapter.notifyDataSetChanged()
        binding.noAppointmentError.isVisible = list.isEmpty()
        binding.rvAppointment.isVisible = list.isNotEmpty()

    }


}