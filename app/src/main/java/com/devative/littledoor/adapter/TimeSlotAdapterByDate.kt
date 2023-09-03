package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.databinding.ItemChipBinding
import com.devative.littledoor.databinding.ItemTherapistBinding
import com.devative.littledoor.model.AvailableSlotModel
import com.devative.littledoor.model.TimeSLotModel


class TimeSlotAdapterByDate(
    val context: Context,
    val list: ArrayList<AvailableSlotModel.Data>,
    val event: TimeSlotAdapterByDateEvent
) : RecyclerView.Adapter<TimeSlotAdapterByDate.ViewHolder>() {

    private var selectedPosition = -1
    inner class ViewHolder(val binding: ItemChipBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            binding.root.setOnClickListener {
                if (list[position].is_booked != 1) {
                    selectedPosition = position
                    notifyDataSetChanged()
                    event.onItemSelected(list[position])
                }
            }
            if (list[position].is_booked == 1) {
                binding.chip.setBackgroundResource(R.color.primary)
                binding.txtName.setTextColor(
                    ContextCompat.getColorStateList(
                        context,
                        R.color.neutral_white
                    )
                )
            } else if (selectedPosition == position) {
                binding.chip.setBackgroundResource(R.color.secondary)
                binding.txtName.setTextColor(
                    ContextCompat.getColorStateList(
                        context,
                        R.color.neutral_white
                    )
                )
            } else {
                binding.chip.setBackgroundResource(R.color.grey_lighter)
                binding.txtName.setTextColor(
                    ContextCompat.getColorStateList(
                        context,
                        R.color.grey_primary
                    )
                )
            }
            binding.txtName.text = list[position].slot_time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemChipBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeSlotAdapterByDate.ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getSelected() :AvailableSlotModel.Data {
        return list[selectedPosition]
    }
    interface TimeSlotAdapterByDateEvent{
        fun onItemSelected(data:AvailableSlotModel.Data)
    }

}