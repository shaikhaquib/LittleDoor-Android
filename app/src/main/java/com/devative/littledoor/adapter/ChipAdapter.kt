package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.databinding.ItemChipBinding
import com.devative.littledoor.databinding.ItemTherapistBinding


class ChipAdapter(
    val context: Context,
    val list:ArrayList<String>,
    val event: ChipAdapterEvent
) : RecyclerView.Adapter<ChipAdapter.ViewHolder>() {
    var selectItem = -1
    inner class ViewHolder(val binding: ItemChipBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            binding.root.setOnClickListener {
                selectItem =position
                notifyDataSetChanged()
            }
            if (selectItem == position){
                binding.chip.setBackgroundResource(R.color.primary)
                binding.txtName.setTextColor(ContextCompat.getColorStateList(context,R.color.neutral_white))
            }else{
                binding.chip.setBackgroundResource(R.color.grey_lighter)
                binding.txtName.setTextColor(ContextCompat.getColorStateList(context,R.color.grey_primary))
            }
            binding.txtName.text = list[position]
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemChipBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChipAdapter.ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ChipAdapterEvent {
        fun onclick(position: Int)
    }
}