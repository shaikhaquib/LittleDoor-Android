package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.databinding.ItemCreateSlotBinding
import com.devative.littledoor.model.SessionDetails
import com.devative.littledoor.util.GeneralBottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Arrays


class THCreateSlotAdapter(
    val context: AppCompatActivity,
    val slotTime: List<SessionDetails.Data.SlotTime>,
    val event:THCreateSlotAdapterEvent
) : RecyclerView.Adapter<THCreateSlotAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemCreateSlotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            val data = slotTime[position]
            binding.txtDay.text = data.day
            handleTimeSet(data.slots.isNotEmpty())
            binding.chkDays.isSelected = data.slots.isNotEmpty()
            handleDataVisibility(binding.chkDays.isSelected)
            binding.chkDays.setOnClickListener {
                if (binding.chkDays.isSelected && data.slots.isNotEmpty()){
                    val bottomSheetDialog = GeneralBottomSheetDialog()
                    bottomSheetDialog.setTitle("Clear Slots")
                    bottomSheetDialog.setMessage("This Action will clear all selected slots for the day, Do you wants to continue?")
                    bottomSheetDialog.setNegativeButton("cancel") {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.setPositiveButton("continue") {
                        event.clearSelection(position)
                        binding.chkDays.isSelected = false
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show(context.supportFragmentManager, "BottomSheetDialog")
                }else {
                    binding.chkDays.isSelected = !binding.chkDays.isSelected
                    handleDataVisibility(binding.chkDays.isSelected)
                }
            }

            binding.txtSelectTime.setOnClickListener {
                event.onOpenSelection(position)
            }
            binding.chkEdit.setOnClickListener {
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
        return slotTime.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface THCreateSlotAdapterEvent {
        fun onclick(position: Int)
        fun onOpenSelection(day:Int)
        fun clearSelection(day:Int)
    }
}