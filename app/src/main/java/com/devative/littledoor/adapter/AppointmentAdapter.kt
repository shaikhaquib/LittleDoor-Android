package com.devative.littledoor.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.databinding.ItemAppointmentBinding
import com.devative.littledoor.model.UserAppointmentModel

class AppointmentAdapter(
    private val context: Context,
    private val list :ArrayList<UserAppointmentModel.Data>,
    private val explorerAdapterEvent: AppointmentAdapterEvent,
    private val maxLength: Int = -1
) : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemAppointmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            val item = list[position]
            binding.txtName.text = item.patient_name
            binding.imgProfile.load(item.patient_profile)
            binding.txtDesc.text = Constants.getTimeRemaining("${item.apointmnet_date}, ${item.slot_time}")
            binding.txtDate.text = "${item.slot_time}"

            if (Constants.hasDatePassed(item.apointmnet_date, item.slot_time)){
                binding.txtDate.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemAppointmentBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position)
    }


    override fun getItemCount(): Int {
        return if (maxLength == -1)
            list.size
        else if (list.size > 2)
            2
        else
            list.size
    }

    interface AppointmentAdapterEvent {
        fun onclick(position: Int)
    }
}