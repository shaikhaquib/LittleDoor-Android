package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.databinding.ItemTherapistBinding

class TherapistAdapter(
    val context: Context,
    val therapistAdapterEvent:TherapistAdapterEvent
) : RecyclerView.Adapter<TherapistAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemTherapistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemTherapistBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return 10
    }

    interface TherapistAdapterEvent {
        fun onclick(position: Int)
    }
}