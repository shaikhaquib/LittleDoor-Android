package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.databinding.ItemChatBinding
import com.devative.littledoor.databinding.ItemDailyGeneralBinding
import com.devative.littledoor.databinding.ItemExploreBinding
import com.devative.littledoor.databinding.ItemTherapistBinding


class DailyGeneralAdapter(
    val context: Context,
    val event:DailyGeneralAdapterEvent
) : RecyclerView.Adapter<DailyGeneralAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemDailyGeneralBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDailyGeneralBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyGeneralAdapter.ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return 4
    }

    interface DailyGeneralAdapterEvent {
        fun onclick(position: Int)
    }
}