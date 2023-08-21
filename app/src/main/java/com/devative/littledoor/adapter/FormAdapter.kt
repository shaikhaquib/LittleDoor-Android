package com.devative.littledoor.adapter

import FilePickerUtils
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.activity.drForms.ActivityAddAppreciation
import com.devative.littledoor.activity.drForms.ActivityAddEducationForm
import com.devative.littledoor.activity.drForms.ActivityAddExperience
import com.devative.littledoor.activity.drForms.ActivityUploadOtherDocument
import com.devative.littledoor.databinding.ItemFormDataBinding
import com.devative.littledoor.model.DoctorDetailsResponse

/**
 * Created by AQUIB RASHID SHAIKH on 16-07-2023.
 */
class FormAdapter (
    val context: Context,
    val list: ArrayList<Any>,
    val event: FormAdapterEvent,
    val isEdit:Boolean = true
) : RecyclerView.Adapter<FormAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemFormDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {

            binding.imgIcon.isVisible = isEdit

            if (list.isNotEmpty())
            if (list[0] is ActivityAddExperience.FormData){
                handleFormExp(position)
            }else if (list[0] is DoctorDetailsResponse.Data.WorkExperience){
                formList(position)
            }else if (list[0] is DoctorDetailsResponse.Data.Education){
                handleFormEduList(position)
            }else if (list[0] is ActivityAddEducationForm.EducationFormData){
                handleFormEdu(position)
            }else if (list[0] is DoctorDetailsResponse.Data.Addres){
                handleAddress(position)
            }else if (list[0] is DoctorDetailsResponse.Data.Appreciation){
                appreciationList(position)
            }else if (list[0] is ActivityAddAppreciation.AppreciationForm){
                appreciationForm(position)
            }else if (list[0] is DoctorDetailsResponse.Data.Other){
                otherDocument(position)
            }else if (list[0] is ActivityUploadOtherDocument.OtherDocumentForm){
                otherDocumentForm(position)
            }
            binding.imgIcon.setOnClickListener {
                event.onclick(position, list[position])
            }
        }

        private fun otherDocumentForm(position: Int) {
            val formData = list[position] as ActivityUploadOtherDocument.OtherDocumentForm
            val fileName =  if (formData.certificateURL != null){
                formData.certificateURL?.substring(formData.certificateURL!!.lastIndexOf("/")+1)
            }else{
                FilePickerUtils.getFileNameFromUri(formData.certificate!!,context)
            }
            setData(formData.name!!,fileName!!,"")
            binding.view.visibility = View.VISIBLE
            binding.imageView7.visibility = View.VISIBLE
            binding.txtSubDesc.visibility = View.GONE
        }

        private fun otherDocument(position: Int) {
            removeCardElevation()
            val formData = list[position] as DoctorDetailsResponse.Data.Other
            setData(formData.name,formData.document.substring(formData.document.lastIndexOf("/")+1),"")
            binding.view.visibility = View.VISIBLE
            binding.imageView7.visibility = View.VISIBLE
        }

        private fun appreciationList(position: Int) {
            removeCardElevation()
            val formData = list[position] as DoctorDetailsResponse.Data.Appreciation
            setData(formData.name!!,formData.category_achieved!!,"${formData.description}, Issued on: ${formData.issue_date}")
        }

        private fun appreciationForm(position: Int) {
            val formData = list[position] as ActivityAddAppreciation.AppreciationForm
            setData(formData.name!!,formData.category_achieved!!,"${formData.description}, Issued on: ${formData.issue_date}")
        }

        private fun handleAddress(position: Int) {
            removeCardElevation()
            val formData = list[position] as DoctorDetailsResponse.Data.Addres
            setData(formData.address_line_1,formData.address_line_1,"${formData.city_name}, ${formData.pincode}, ${formData.state_name}")

        }

        private fun handleFormEduList(position: Int) {
            removeCardElevation()
            val formData = list[position] as DoctorDetailsResponse.Data.Education
            setData(formData.name,formData.institution_name,"${formData.start_date} - ${formData.start_date}")
        }

        private fun setData(name:String,txtDesc:String,txtSubDesc:String) {
            binding.txtName.text = name
            binding.txtSubDesc.text = txtSubDesc
            binding.txtDesc.text = txtDesc
        }

        private fun handleFormEdu(position: Int) {
            val formData = list[position] as ActivityAddEducationForm.EducationFormData
            setData(formData.name!!,formData.institution_name!!,"${formData.start_date} - ${formData.start_date}")
        }

        private fun handleFormExp(position: Int) {
            val formData = list[position] as ActivityAddExperience.FormData
            setData(formData.getDescription()!!,formData.getDescription()!!,"Year Experience ${formData.getYearOfExperience()}")
        }
        private fun formList(position: Int) {
            removeCardElevation()
            val formData = list[position] as DoctorDetailsResponse.Data.WorkExperience
            setData(formData.category_name,
                formData.description,
                getCommaSeparatedNames(formData.sub_category)
            )
        }
    }

    private fun ViewHolder.removeCardElevation() {
        binding.cardRoot.setCornerRadius(0f)
        binding.cardRoot.elevation = 0f
        val param = binding.cardRoot.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(-16, -16, -16, -16)
        binding.cardRoot.layoutParams = param
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