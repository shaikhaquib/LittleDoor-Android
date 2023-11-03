package com.devative.littledoor.activity

import android.app.Notification
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.devative.littledoor.adapter.UserAppointmentListAdapter
import com.devative.littledoor.adapter.UserNotificationListAdapter
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityUserNotificationActicityBinding
import com.devative.littledoor.model.NotificationResponse
import com.devative.littledoor.util.ListSpacingDecoration

class UserNotificationActivity : BaseActivity() {
    private val binding: ActivityUserNotificationActicityBinding by lazy {
        ActivityUserNotificationActicityBinding.inflate(layoutInflater)
    }
    private val mainViewModel: MainViewModel by viewModels()
    private val notificationList = ArrayList<NotificationResponse.Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.rvAppointment.addItemDecoration(ListSpacingDecoration())
        binding.rvAppointment.adapter = UserNotificationListAdapter(this, notificationList, object :
            UserNotificationListAdapter.UserNotificationListAdapterEvent {
            override fun onclick(position: Int) {
                val dataMap = HashMap<String, Any>()
                dataMap["notification_id"] = notificationList[position].id
                dataMap["is_read"] = 1
                mainViewModel.notificationReadReceipt(dataMap)
            }
        })
        mainViewModel.getNotifications()
        observe()
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

    fun observe() {
        mainViewModel.getNotifications.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        notificationList.clear()
                        notificationList.addAll(it.data.data)
                        binding.rvAppointment.adapter?.notifyDataSetChanged()
                    }
                    binding.rvAppointment.isVisible = notificationList.isNotEmpty()
                    binding.emptyView.isVisible = notificationList.isEmpty()
                }

                Status.ERROR -> {
                    progress.dismiss()
                    binding.rvAppointment.isVisible = notificationList.isNotEmpty()
                    binding.emptyView.isVisible = notificationList.isEmpty()
                }

            }
        }

    }


}