package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.databinding.DoctorFormMasterItemBinding
import com.devative.littledoor.databinding.ItemDoctorDetailsBinding
import com.devative.littledoor.model.DoctorDetailsResponse
import com.devative.littledoor.model.DoctotorListRes
import com.devative.littledoor.model.DrRegistrationMasterModel
import com.google.android.material.chip.Chip

/**
 * Created by AQUIB RASHID SHAIKH on 02-07-2023.
 */
class DoctorDetailsAdapter(
    val context: Context,
    var formData: DoctotorListRes.Data,
    val event: FormMasterEvent
) : RecyclerView.Adapter<DoctorDetailsAdapter.ViewHolder>() {

    val formList = arrayListOf("Expertise","Language","Appreciation")

    inner class ViewHolder(val binding: ItemDoctorDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            binding.txtName.text = formList[position]
            binding.addForm.visibility = View.GONE
            when(formList[position]){
               /* "Work experience" -> workEXP()
                "Education" -> education()*/
                "Expertise" -> expertise()
               /* "Address" -> address()*/
                "Language" -> language()
                "Appreciation" -> appreciation()
               // "Other documents" -> otherDocument()
            }
            binding.addForm.setOnClickListener{event.onClickAdd(position)}
        }

/*
        private fun otherDocument() {
            binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.other_doc))
            formData?.let {
                if (!it.data.other.isNullOrEmpty()) {
                    
                    binding.rvFormData.visibility = View.VISIBLE
                    binding.rvFormData.adapter =
                        FormAdapter(context, it.data.other as ArrayList<Any>, object :
                            FormAdapter.FormAdapterEvent {
                            override fun onclick(
                                position: Int,
                                formData: Any
                            ) {
                                event.onEdit(formData, position)
                            }

                        })
                }
            }

        }
*/

        private fun appreciation() {
            binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.appreciation))
            formData.let {
                if (!it.appreciation.isNullOrEmpty()) {

                    binding.rvFormData.visibility = View.VISIBLE
                    binding.rvFormData.adapter =
                        FormAdapter(context, it.appreciation as ArrayList<Any>, object :
                            FormAdapter.FormAdapterEvent {
                            override fun onclick(
                                position: Int,
                                formData: Any
                            ) {
                                event.onEdit(formData, position)
                            }

                        },false)

                }
            }
        }

        private fun language() {
            binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.lang))
            formData.apply {
                val chipList = ArrayList<String>()
                binding.chipGroup.removeAllViews()
                if (!languages.isNullOrEmpty()) {
                    for (chip in languages) {
                        chipList.add(chip)
                        binding.chipGroup.addView(
                            Chip(context).apply {
                                text = chip
                                setBackgroundColor(ContextCompat.getColor(context, R.color.chipback))
                                isCloseIconVisible = false
                                setTextColor(ContextCompat.getColor(context, R.color.black))
                                setTextAppearance(R.style.TextTitleNormal_12sp)
                                chipCornerRadius = 15f
                                setPadding(24)
                                setOnCloseIconClickListener {
                                    binding.chipGroup.removeView(this)
                                    chipList.remove(text)
                                    event.onLangRemove(chipList)
                                }
                            }
                        )
                    }
                }
                binding.chipGroup.isVisible = chipList.isNotEmpty()
            }

        }

/*
        private fun address() {
            binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.address))
            formData?.let {
                if (!it.data.address.isNullOrEmpty()) {
                    
                    binding.rvFormData.visibility = View.VISIBLE
                    binding.rvFormData.adapter =
                        FormAdapter(context, it.data.address as ArrayList<Any>, object :
                            FormAdapter.FormAdapterEvent {
                            override fun onclick(
                                position: Int,
                                formData: Any
                            ) {
                                event.onEdit(formData, position)
                            }

                        })
                }
            }
        }
*/

        private fun expertise() {
            binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.experties))
            formData.apply {
                val chipList = ArrayList<String>()
                binding.chipGroup.removeAllViews()
                if (!skills.isNullOrEmpty())
                    for (chip in skills) {
                        chipList.add(chip.skill_name)
                        binding.chipGroup.addView(
                            Chip(context).apply {
                                text = chip.skill_name
                                setBackgroundColor(ContextCompat.getColor(context, R.color.chipback))
                                isCloseIconVisible = false
                                setTextColor(ContextCompat.getColor(context, R.color.black))
                                setTextAppearance(R.style.TextTitleNormal_12sp)
                                chipCornerRadius = 15f
                                setPadding(24)
                                setOnCloseIconClickListener {
                                    binding.chipGroup.removeView(this)
                                    chipList.remove(text)
                                    event.onExpertiseRemove(chipList)
                                }
                            }
                        )
                    }
                binding.chipGroup.isVisible = chipList.isNotEmpty()
            }
        }

/*
        private fun workEXP() {
            binding.icForm.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.edu))
            formData?.let {
                if (!it.data.work_experience.isNullOrEmpty()) {
                    
                    binding.rvFormData.visibility = View.VISIBLE
                    binding.rvFormData.adapter =
                        FormAdapter(context, it.data.work_experience as ArrayList<Any>, object :
                            FormAdapter.FormAdapterEvent {
                            override fun onclick(
                                position: Int,
                                formData: Any
                            ) {
                                event.onEdit(formData, position)
                            }

                        })
                }
            }
        }
*/
/*
        private fun education() {
            binding.icForm.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exp))
            formData?.let {
                if (!it.data.education.isNullOrEmpty()) {
                    
                    binding.rvFormData.visibility = View.VISIBLE
                    binding.rvFormData.adapter =
                        FormAdapter(context, it.data.education as ArrayList<Any>, object :
                            FormAdapter.FormAdapterEvent {
                            override fun onclick(
                                position: Int,
                                formData: Any
                            ) {
                                event.onEdit(formData, position)
                            }

                        })
                }
            }
        }
*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDoctorDetailsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoctorDetailsAdapter.ViewHolder, position: Int) {
        holder.bindData(position)

    }

    override fun getItemCount(): Int {
        return formList.size
    }
    interface FormMasterEvent {
        fun onClick(position: Int)
        fun onClickAdd(position: Int)
        fun onEdit(type: Any, position: Int)
        fun onExpertiseRemove(list: ArrayList<String>)
        fun onLangRemove(list: ArrayList<String>)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}