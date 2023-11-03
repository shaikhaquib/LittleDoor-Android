package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.databinding.ItemTherapistBinding
import com.devative.littledoor.model.DoctotorListRes

class TherapistAdapter(
    val context: Context,
    val dataList:ArrayList<DoctotorListRes.Data>,
    val therapistAdapterEvent:TherapistAdapterEvent
) : RecyclerView.Adapter<TherapistAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemTherapistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int, data: DoctotorListRes.Data) {

            binding.root.setOnClickListener{
                therapistAdapterEvent.onclick(position)
            }

            binding.apply {
                txtName.text = data.name
                txtDesc.text = "${data.category_name}, ${data.city}, ${data.state}"
                data.image?.let { imgProfile.load(it) }
            }

            binding.btnBookAppointment.setOnClickListener {
                therapistAdapterEvent.bookAppointment(position)
            }
            binding.btnChat.setOnClickListener {
                therapistAdapterEvent.onChat(position,data)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemTherapistBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position,dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface TherapistAdapterEvent {
        fun onclick(position: Int)
        fun bookAppointment(position: Int)
        fun onChat(position: Int, data: DoctotorListRes.Data)
    }
}