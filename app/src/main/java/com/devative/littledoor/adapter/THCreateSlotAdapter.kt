package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.databinding.ItemCreateSlotBinding
import java.util.Arrays


class THCreateSlotAdapter(
    val context: Context,
    val event:THCreateSlotAdapterEvent
) : RecyclerView.Adapter<THCreateSlotAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemCreateSlotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            val weekDays: Array<String> = context.resources.getStringArray(R.array.week_days)
            val weekDaysList = Arrays.asList(*weekDays)
            val day = weekDaysList[position] // second day of the week is Tuesday
            binding.txtDay.text = day
            binding.chkEdit.isSelected = false
            binding.chkEdit.setOnClickListener {
                binding.chkEdit.isSelected =  !binding.chkEdit.isSelected
            }
            binding.txtSelectTime.setOnClickListener {
                event.onOpenSelection(position)
            }
        }
        fun handleDataVisibility(checked:Boolean){
            binding.txtSelectTime.isVisible = checked
            binding.chkEdit.isVisible = checked
        }
        fun handleTimeSet(checked:Boolean){
            if (checked) {
                binding.txtSelectTime.text = "Time Set"
                binding.txtSelectTime.isClickable = false
                binding.chkEdit.isClickable = true
                binding.chkEdit.isSelected = true
                binding.txtSelectTime.setTextColor(ContextCompat.getColor(context, R.color.black))
            }else {
                binding.txtSelectTime.text = "Select Time"
                binding.txtSelectTime.isClickable = true
                binding.chkEdit.isClickable = false
                binding.chkEdit.isSelected = false
                binding.txtSelectTime.setTextColor(ContextCompat.getColor(context, R.color.primary))
            }
            binding.imgSet.isVisible = checked
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCreateSlotBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: THCreateSlotAdapter.ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return 7
    }

    interface THCreateSlotAdapterEvent {
        fun onclick(position: Int)
        fun onOpenSelection(day:Int)
    }
}