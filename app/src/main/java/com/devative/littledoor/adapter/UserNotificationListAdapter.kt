package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.databinding.ItemUserAppointmentBinding
import com.devative.littledoor.databinding.ItemUserNotificationBinding
import com.devative.littledoor.databinding.ItemUserTransactionBinding

class UserNotificationListAdapter(
    private val context: Context,
    private val event: UserNotificationListAdapterEvent,
    private val maxLength: Int = -1
) : RecyclerView.Adapter<UserNotificationListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemUserNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
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
        return if (maxLength == -1)
            3
        else
            2
    }

    interface UserNotificationListAdapterEvent {
        fun onclick(position: Int)
    }
}