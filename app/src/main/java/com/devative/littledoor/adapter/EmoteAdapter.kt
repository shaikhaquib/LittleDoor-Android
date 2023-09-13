package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.databinding.ItemChatBinding
import com.devative.littledoor.databinding.ItemEmoteBinding
import com.devative.littledoor.databinding.ItemExploreBinding
import com.devative.littledoor.databinding.ItemTherapistBinding
import com.devative.littledoor.model.EmotModel


class EmoteAdapter(
    val context: Context,
    val list:ArrayList<EmotModel.Data>,
    val event:EmoteAdapterEvent,
    var selectedPosition:Int = -1
) : RecyclerView.Adapter<EmoteAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemEmoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            binding.txtName.text = list[position].name
            binding.imgEmote.load(list[position].image_url,R.drawable.sad)
            if (selectedPosition == position){
                binding.txtName.setTextColor(ContextCompat.getColor(context, R.color.primary))
            }else{
                binding.txtName.setTextColor(ContextCompat.getColor(context, R.color.grey_primary))
            }

            binding.root.setOnClickListener {
                selectedPosition = position
                event.onclick(position)
                notifyDataSetChanged()
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemEmoteBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmoteAdapter.ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface EmoteAdapterEvent {
        fun onclick(position: Int)
    }
}