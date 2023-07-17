package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.databinding.DoctorFormMasterItemBinding
import com.devative.littledoor.model.DoctorDetailsResponse
import com.devative.littledoor.model.DrRegistrationMasterModel

/**
 * Created by AQUIB RASHID SHAIKH on 02-07-2023.
 */
class DoctorFormMasterAdapter(
    val context: Context,
    val formList:ArrayList<DrRegistrationMasterModel>,
    var formData:DoctorDetailsResponse?,
    val event: FormMasterEvent
) : RecyclerView.Adapter<DoctorFormMasterAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: DoctorFormMasterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            binding.txtName.text = formList[position].name
            when(formList[position].name){
                "Work experience" ->{
                    binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.exp))
                    formData?.let {
                        if (!it.data.work_experience.isNullOrEmpty()){
                            binding.formDivider.visibility = View.VISIBLE
                            binding.rvFormData.visibility = View.VISIBLE
                            binding.rvFormData.adapter = FormAdapter(context,it.data.work_experience as ArrayList<Any>,object :
                                FormAdapter.FormAdapterEvent {
                                override fun onclick(
                                    position: Int,
                                    formData: Any
                                ) {
                                    event.onEdit(formData,position)
                                }

                            })
                        }
                    }
                }
                "Education" -> binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.edu))
                "Expertise" -> binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.experties))
                "Address" -> binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.address))
                "Language" -> binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.lang))
                "Appreciation" -> binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.appreciation))
                "Other documents" -> binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.other_doc))
            }
            binding.addForm.setOnClickListener{event.onClickAdd(position)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            DoctorFormMasterItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoctorFormMasterAdapter.ViewHolder, position: Int) {
        holder.bindData(position)

    }

    override fun getItemCount(): Int {
        return formList.size
    }

    fun setBindFormData( formData:DoctorDetailsResponse?){
        this.formData = formData
        notifyDataSetChanged()
    }

    interface FormMasterEvent {
        fun onClick(position: Int)
        fun onClickAdd(position: Int)
        fun onEdit(type: Any, position: Int)
    }

}