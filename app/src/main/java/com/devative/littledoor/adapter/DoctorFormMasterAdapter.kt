package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.databinding.DoctorFormMasterItemBinding
import com.devative.littledoor.databinding.ItemChatBinding
import com.devative.littledoor.model.DrRegistrationMasterModel

/**
 * Created by AQUIB RASHID SHAIKH on 02-07-2023.
 */
class DoctorFormMasterAdapter(
    val context: Context,
    val formList:ArrayList<DrRegistrationMasterModel>,
    val event: FormMasterEvent
) : RecyclerView.Adapter<DoctorFormMasterAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: DoctorFormMasterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            DoctorFormMasterItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoctorFormMasterAdapter.ViewHolder, position: Int) {
        holder.bindData(position)
        holder.binding.txtName.text = formList[position].name
        when(formList[position].name){
            "Work experience" -> holder.binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.exp))
            "Education" -> holder.binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.edu))
            "Expertise" -> holder.binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.experties))
            "Address" -> holder.binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.address))
            "Language" -> holder.binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.lang))
            "Appreciation" -> holder.binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.appreciation))
            "Other documents" -> holder.binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.other_doc))
        }
        holder.binding.addForm.setOnClickListener{event.onClickAdd(position)}
    }

    override fun getItemCount(): Int {
        return formList.size
    }


    interface FormMasterEvent {
        fun onClick(position: Int)
        fun onClickAdd(position: Int)
    }

}