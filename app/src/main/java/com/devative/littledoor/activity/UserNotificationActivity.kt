package com.devative.littledoor.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.devative.littledoor.adapter.UserAppointmentListAdapter
import com.devative.littledoor.adapter.UserNotificationListAdapter
import com.devative.littledoor.databinding.ActivityUserNotificationActicityBinding
import com.devative.littledoor.util.ListSpacingDecoration

class UserNotificationActivity : AppCompatActivity() {
    private val binding : ActivityUserNotificationActicityBinding by lazy {
        ActivityUserNotificationActicityBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.rvAppointment.addItemDecoration(ListSpacingDecoration())
        binding.rvAppointment.adapter = UserNotificationListAdapter(this,object:
            UserNotificationListAdapter.UserNotificationListAdapterEvent {
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