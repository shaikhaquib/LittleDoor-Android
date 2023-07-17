package com.devative.littledoor.adapter

import FilePickerUtils
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import carbon.widget.LinearLayout
import com.devative.littledoor.activity.drForms.ActivityAddExperience
import com.devative.littledoor.databinding.ItemFormDataBinding
import com.devative.littledoor.model.DoctorDetailsResponse

/**
 * Created by AQUIB RASHID SHAIKH on 16-07-2023.
 */
class FormAdapter (
    val context: Context,
    val list: ArrayList<Any>,
    val event: FormAdapterEvent
) : RecyclerView.Adapter<FormAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemFormDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            if (list.isNotEmpty())
            if (list[0] is ActivityAddExperience.FormData){
                handleFormExp(position)
            }else if (list[0] is DoctorDetailsResponse.Data.WorkExperience){
                formList(position)
            }

        }

        private fun handleFormExp(position: Int) {
            val formData = list[position] as ActivityAddExperience.FormData
            binding.txtName.text = formData.getDescription()
            binding.txtDesc.text = "Year Experience ${formData.getYearOfExperience()}"
            binding.txtSubDesc.text = FilePickerUtils.getFileNameFromUri(formData.getCertificate()!!, context)
            binding.imgIcon.setOnClickListener {
                event.onclick(position, list[position])
            }
        }
        private fun formList(position: Int) {
            binding.cardRoot.setCornerRadius(0f)
            binding.cardRoot.elevation = 0f

            val param = binding.cardRoot.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(-16,-16,-16,-16)
            binding.cardRoot.layoutParams = param


            val formData = list[position] as DoctorDetailsResponse.Data.WorkExperience
            binding.txtName.text = formData.category_name
            binding.txtSubDesc.text = getCommaSeparatedNames(formData.sub_category)
            binding.txtDesc.text = formData.certificate.get(0).substring(formData.certificate.get(0).lastIndexOf("/")+1)
            binding.imgIcon.setOnClickListener {
                event.onclick(position, list[position])
            }

        }
    }
    fun getCommaSeparatedNames(subCategories: List<DoctorDetailsResponse.Data.WorkExperience.SubCategory>): String {
        return subCategories.map { it.name }.joinToString(", ")
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFormDataBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FormAdapter.ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface FormAdapterEvent {
        fun onclick(position: Int, formData: Any)
    }
}