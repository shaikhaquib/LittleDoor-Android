package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.databinding.ItemUserAppointmentBinding
import com.devative.littledoor.databinding.ItemUserTransactionBinding

class UserTransactionListAdapter(
    private val context: Context,
    private val event: UserTransactionListAdapterEvent,
    private val maxLength: Int = -1
) : RecyclerView.Adapter<UserTransactionListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemUserTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemUserTransactionBinding.inflate(LayoutInflater.from(context), parent, false)
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

    interface UserTransactionListAdapterEvent {
        fun onclick(position: Int)
    }
}