package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.databinding.ItemChatBinding
import com.devative.littledoor.databinding.ItemTherapistBinding
import com.devative.littledoor.model.ChatListResponse


class ChatRoomAdapter(
    val context: Context,
    val chatList: ArrayList<ChatListResponse.Data>,
    val event: ChatRoomAdapterEvent
) : RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            val chat = chatList[position]
            binding.txtName.text = if (Constants.isDoctor) chat.patient_name else chat.doctor_name
            binding.imageView5.load((if (Constants.isDoctor) chat.patient_image_url else chat.doctor_image_url).toString())
            itemView.setOnClickListener { event.onclick(position) }
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
        return chatList.size
    }

    interface ChatRoomAdapterEvent {
        fun onclick(position: Int)
    }
}