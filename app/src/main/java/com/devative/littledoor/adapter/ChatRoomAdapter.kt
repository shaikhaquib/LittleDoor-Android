package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.databinding.ItemChatBinding
import com.devative.littledoor.databinding.ItemTherapistBinding


class ChatRoomAdapter(
    val context: Context,
    val event:ChatRoomAdapterEvent
) : RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemChatBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatRoomAdapter.ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return 10
    }

    interface ChatRoomAdapterEvent {
        fun onclick(position: Int)
    }
}