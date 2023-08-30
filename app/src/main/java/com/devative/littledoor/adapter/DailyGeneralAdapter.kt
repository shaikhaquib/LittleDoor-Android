package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.databinding.ItemChatBinding
import com.devative.littledoor.databinding.ItemDailyGeneralBinding
import com.devative.littledoor.databinding.ItemExploreBinding
import com.devative.littledoor.databinding.ItemTherapistBinding
import com.devative.littledoor.model.DailyJournalModel


class DailyGeneralAdapter(
    val context: Context,
    val event:DailyGeneralAdapterEvent,
    val list:ArrayList<DailyJournalModel.Data>
) : RecyclerView.Adapter<DailyGeneralAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemDailyGeneralBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            binding.txtDetails.text = list[position].message
            binding.txtTime.text = list[position].created_at.split(" ")[1]
            when(list[position].emotion_name.toLowerCase()){
                "angry" -> binding.imgEmotion.setImageResource(R.drawable.angry)
                "calm" -> binding.imgEmotion.setImageResource(R.drawable.relieved_face)
                "manic" -> binding.imgEmotion.setImageResource(R.drawable.manic)
                "sad" -> binding.imgEmotion.setImageResource(R.drawable.sad)
                "happy" -> binding.imgEmotion.setImageResource(R.drawable.happy_emoji)
            }
            binding.more.setOnClickListener {
                event.onMore(list[position])
            }
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
        return list.size
    }

    interface DailyGeneralAdapterEvent {
        fun onclick(position: Int)
        fun onMore(position: DailyJournalModel.Data)
    }
}