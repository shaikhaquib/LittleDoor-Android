package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants.isDoctor
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.databinding.ItemChatBinding
import com.devative.littledoor.model.ChatListResponse
import java.util.Locale


class ChatRoomAdapter(
    val context: Context,
    val chatList: ArrayList<ChatListResponse.Data>,
    val event: ChatRoomAdapterEvent
) : RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>() {
    private var filteredItems = chatList

    inner class ViewHolder(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            val chat = filteredItems[position]
            binding.txtName.text = if (isDoctor) chat.patient_name else chat.doctor_name
            binding.imageView5.load(
                (if (isDoctor) chat.patient_image_url else chat.doctor_image_url).toString(),
                (if (isDoctor) R.drawable.user_default_icon else R.drawable.therapist_default_icon),

                )
            itemView.setOnClickListener { event.onclick(position) }
            binding.txtDate.visibility =  View.GONE
            binding.txtDesc.visibility =  View.GONE
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
        return filteredItems.size
    }

    interface ChatRoomAdapterEvent {
        fun onclick(position: Int)
    }

    fun filter(query: String) {
        filteredItems = ArrayList()
        if (query.isNotEmpty()) {
            for (item in chatList) {
                val name = if (isDoctor) {
                    item.patient_name
                } else item.doctor_name
                if (name.lowercase(Locale.getDefault()).contains(
                        query.lowercase(
                            Locale.getDefault()
                        )
                    )
                ) {
                    filteredItems.add(item)
                }
            }
        } else {
            filteredItems = chatList
        }
        notifyDataSetChanged()
    }

}