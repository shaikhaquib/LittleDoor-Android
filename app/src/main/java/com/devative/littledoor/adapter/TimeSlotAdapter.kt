package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.databinding.ItemChipBinding
import com.devative.littledoor.databinding.ItemTherapistBinding
import com.devative.littledoor.model.TimeSLotModel


class TimeSlotAdapter(
    val context: Context,
    val list: ArrayList<TimeSLotModel.Data>,
) : RecyclerView.Adapter<TimeSlotAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemChipBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            binding.root.setOnClickListener {
                list[position].isSelected = !list[position].isSelected
                notifyDataSetChanged()
            }
            if (list[position].isSelected) {
                binding.chip.setBackgroundResource(R.color.primary)
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

    override fun onBindViewHolder(holder: TimeSlotAdapter.ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getSelected(): List<TimeSLotModel.Data> {
        return list.filter { it.isSelected }
    }

    fun setSelected(selected: List<Int>) {
        for (data in list) {
            data.isSelected = data.id in selected
        }

    }
}