package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.databinding.ItemUserAppointmentBinding
import com.devative.littledoor.databinding.ItemUserNotificationBinding
import com.devative.littledoor.databinding.ItemUserTransactionBinding
import com.devative.littledoor.model.NotificationResponse

class UserNotificationListAdapter(
    private val context: Context,
    private val notificationList: ArrayList<NotificationResponse.Data>,
    private val event: UserNotificationListAdapterEvent,
    private val maxLength: Int = -1
) : RecyclerView.Adapter<UserNotificationListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemUserNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            val notification = notificationList[position]
            binding.txtName.text = notification.notification_type
            binding.txtSubText.text = notification.message
            itemView.setOnClickListener{event.onclick(position)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemUserNotificationBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position)
    }


    override fun getItemCount(): Int {
        return notificationList.size
    }

    interface UserNotificationListAdapterEvent {
        fun onclick(position: Int)
    }
}