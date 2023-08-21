package com.devative.littledoor.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.devative.littledoor.R
import com.devative.littledoor.adapter.AppointmentAdapter
import com.devative.littledoor.adapter.UserAppointmentListAdapter
import com.devative.littledoor.databinding.ActivityUserAppointmentBinding
import com.devative.littledoor.util.ListSpacingDecoration

class UserAppointmentActivity : AppCompatActivity() {
    private val binding : ActivityUserAppointmentBinding by lazy {
        ActivityUserAppointmentBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.rvAppointment.addItemDecoration(ListSpacingDecoration())
        binding.rvAppointment.adapter = UserAppointmentListAdapter(this,object:
            UserAppointmentListAdapter.UserAppointmentAdapterEvent {
            override fun onclick(position: Int) {

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
}