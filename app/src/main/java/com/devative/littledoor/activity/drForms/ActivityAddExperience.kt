package com.devative.littledoor.activity.drForms

import FilePickerUtils
import FilePickerUtils.getFileNameFromUri
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import carbon.widget.Button
import com.devative.littledoor.R
import com.devative.littledoor.activity.BaseActivity
import com.devative.littledoor.adapter.FormAdapter
import com.devative.littledoor.architecturalComponents.apicall.APIClient
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.FileUploader
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DrRegViewModel
import com.devative.littledoor.databinding.ActivityAddExperienceBinding
import com.devative.littledoor.model.CategoryResponse
import com.devative.littledoor.model.DoctorDetailsResponse
import com.devative.littledoor.model.GeneralResponse
import com.devative.littledoor.model.SearchAbleList
import com.devative.littledoor.model.SubCategoryResponse
import com.devative.littledoor.util.Logger
import com.devative.littledoor.util.Progress
import com.devative.littledoor.util.SingleSelectBottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActivityAddExperience : BaseActivity(), View.OnClickListener,FormAdapter.FormAdapterEvent {
    lateinit var binding: ActivityAddExperienceBinding
    var fileURI: Uri? = null
    private val vm: DrRegViewModel by viewModels()
    private val categoryList = ArrayList<CategoryResponse.Data>()
    private val subCategoryList = ArrayList<SubCategoryResponse.Data>()
    private val itemList = mutableListOf<String>()
    private val itemIDList = mutableListOf<String>()
    private var formData = FormData()
    private var formSize = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExperienceBinding.inflate(layoutInflater)
        progress = Progress(this)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        initIntent()
        clickHandle()
        observe()
    }

    private fun initIntent() {
        formSize = intent.getIntExtra(Constants.FORM_LIST_SIZE,0)
        if (intent.hasExtra(Constants.FORM_EDIT_DATA)){
            val editData = intent.getSerializableExtra(Constants.FORM_EDIT_DATA) as DoctorDetailsResponse.Data.WorkExperience
            vm.getSubCategory(editData.category_id.toString())
            binding.chipGroup.removeAllViews()
          //  fileURI = data.getCertificate()
            //addFile()
            itemIDList.clear()
            itemList.clear()
            val subCategoryArray = getCommaSeparatedNames(editData.sub_category).split(",")
            for (subCatID in subCategoryArray!!){
                subCategoryList.clear()
                itemIDList.add(subCatID)
            }
            formData.setCategoryId(editData.category_id)
            formData.setSubCategoryId(getCommaSeparatedNames(editData.sub_category))
        }
    }
    fun getCommaSeparatedNames(subCategories: List<DoctorDetailsResponse.Data.WorkExperience.SubCategory>): String {
        return subCategories.map { it.id }.joinToString(", ")
    }

    private fun observe() {
        vm.getCategory()
        vm.categoryData.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    if (progress?.isShowing() == false)
                        progress?.show()
                }

                Status.SUCCESS -> {
                    progress?.dismiss()
                    if (it.data?.status == true) {
                        categoryList.clear()
                        categoryList.addAll(it.data.data)
                        if (formData.getCategoryId() != null)
                        for (cat in categoryList){
                            if (formData.getCategoryId() == cat.id){
                                binding.btnSelectExpertise.setText(cat.name)
                            }
                        }

                    } else {
                        Toasty.error(applicationContext, getString(R.string.some_thing_went_wrong)).show()
                    }
                }

                Status.ERROR -> {
                    progress?.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
        vm.subCategoryData.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    if (progress?.isShowing() == false)
                        progress?.show()
                }

                Status.SUCCESS -> {
                    progress?.dismiss()
                    if (it.data?.status == true) {
                        subCategoryList.clear()
                        subCategoryList.addAll(it.data.data)

                        if (itemIDList.isNotEmpty()){
                            for (subCat in subCategoryList){
                                if (itemIDList.contains(subCat.id.toString())) {
                                    createChip(subCat.name)
                                }
                            }
                        }
                    } else {
                        Toasty.error(applicationContext, getString(R.string.some_thing_went_wrong)).show()
                    }
                }

                Status.ERROR -> {
                    progress?.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }

        vm.formData.observe(this){
            Toast.makeText(applicationContext, "${vm.doctorDetailsData.value?.data?.data?.work_experience?.size}", Toast.LENGTH_SHORT).show()
            binding.rvFormData.adapter = FormAdapter(this,it as ArrayList<Any>,this)
            binding.formDivider.isGone = it.isNullOrEmpty()
        }

    }

    private fun clickHandle() {
        binding.btnSelectExpertise.setOnClickListener(this)
        binding.btnAreasSpecialization.setOnClickListener(this)
        binding.llUploadFile.setOnClickListener(this)
        binding.txtRemove.setOnClickListener(this)
        binding.btnCreate.setOnClickListener(this)
        binding.liTermsSerive.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnSelectExpertise.id -> {
                val searchData = ArrayList<SearchAbleList>()
                for (i in categoryList){
                    searchData.add(
                        SearchAbleList(i.id,i.name)
                    )
                }
               showDropDown(binding.btnSelectExpertise,searchData)
            }

            binding.btnAreasSpecialization.id -> {
                val searchData = ArrayList<SearchAbleList>()
                for (i in subCategoryList){
                    searchData.add(
                        SearchAbleList(i.id,i.name)
                    )
                }
                showDropDown(binding.btnAreasSpecialization,searchData)
            }

            binding.llUploadFile.id -> {
                FilePickerUtils.pickFileFromFileManager(this)
            }
            binding.txtRemove.id -> {
                binding.clFileLayout.visibility = View.GONE
            }
            binding.btnCreate.id -> {
                validateForm(false)
            }
            binding.liTermsSerive.id -> {
                validateForm(true)
            }
        }
    }

    private fun validateForm(isAddMore:Boolean) {
        when{
            formData.getCategoryId() == null->{
                Toasty.error(this,"Please select your expertise").show()
            }
            itemIDList.isEmpty()->{
                Toasty.error(this,"Please select your area of specialization").show()
            }
            binding.edtExperiance.text.isEmpty()->{
                Toasty.error(this,"Please enter year of experience").show()
            }
            fileURI == null->{
                Toasty.error(this,"Please enter year certificate").show()
            }
            binding.edtDescription.text.isEmpty()->{
                Toasty.error(this,"Please enter year Description").show()
            }
            else->{
                formData.setSubCategoryId(itemIDList.joinToString(", "))
                formData.setYearOfExperience( binding.edtExperiance.text.toString())
                formData.setCertificate(fileURI)
                formData.setDescription(binding.edtDescription.text.toString())
                vm.addFormItem(formData)
                binding.apply {
                    edtExperiance.setText("")
                    btnSelectExpertise.setText("Select")
                    edtDescription.setText("")
                    chipGroup.removeAllViews()
                    binding.clFileLayout.visibility = View.GONE
                    fileURI = null
                    formData = FormData()
                }
                if (!isAddMore)
                uploadFormData()
                binding.scroll.scrollTo(0,0)
            }
        }
    }
    private fun uploadFormData() {
        progress?.show()
        val list = vm.formData.value
        if(list ==  null){
            Toasty.error(this,"No data found").show()
        }
        val fileMap = HashMap<String,Uri>()
        val dataMap = HashMap<String,String>()
        for (i in list!!.indices){
            dataMap["step"] = "1"
            fileMap["work[${i+formSize}][certificate][0]"] = list[i].getCertificate()!!
            dataMap["work[${i+formSize}][category_id]"] = list[i].getCategoryId().toString()
            dataMap["work[${i+formSize}][sub_category_id]"] = list[i].getSubCategoryId().toString()
            dataMap["work[${i+formSize}][year_of_experience]"] = list[i].getYearOfExperience().toString()
            dataMap["work[${i+formSize}][description]"] = list[i].getDescription().toString()
        }
        Logger.d("TAG", "uploadFormData: ${dataMap.toString()}")
            CoroutineScope(Dispatchers.IO).launch {
            FileUploader.uploadFiles(this@ActivityAddExperience,
                APIClient.THERAPIST_ADD_DETAILS,
                fileMap,
                dataMap,
                object : FileUploader.UploadCallback {
                    override fun onFileUploadError(errorMessage: String?) {
                        progress?.dismiss()
                        Toasty.error(this@ActivityAddExperience,getString(R.string.some_thing_went_wrong)).show()
                        Logger.e("File upload Error",errorMessage)
                    }

                    override fun onAllFilesUploaded(string: String) {
                        progress?.dismiss()
                        Logger.d("File upload",string)
                        val gson =Gson()
                        val data:GeneralResponse = gson.fromJson(string, GeneralResponse::class.java)
                        if (data.status)
                            Toasty.success(this@ActivityAddExperience,data.message).show()
                        else
                            Toasty.error(this@ActivityAddExperience,data.message).show()

                        finish()

                    }
                })

        }
    }

    private fun showDropDown(button: Button,items: ArrayList<SearchAbleList>) {
        val dialog = SingleSelectBottomSheetDialogFragment(
            this,
            items,
            getString(R.string.areas_of_specialization),
            0,
            false
        ) { selectedValue ->
            when(button.id){
                binding.btnSelectExpertise.id ->{
                    button.text = selectedValue.title
                    vm.getSubCategory(selectedValue.position.toString())
                    formData.setCategoryId(selectedValue.position)
                    itemIDList.clear()
                    itemList.clear()
                    binding.chipGroup.removeAllViews()
                }
                binding.btnAreasSpecialization.id ->{
                    itemIDList.add(selectedValue.position.toString())
                    createChip(selectedValue.title)
                }
            }
        }
        dialog.show(supportFragmentManager, "SingleSelectBottomSheetDialogFragment")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FilePickerUtils.PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            fileURI = data?.data
            addFile()

        }
    }

    private fun addFile() {
        val fileName = getFileNameFromUri(fileURI!!, this)
        val path = FilePickerUtils.getFullPathFromUri(fileURI!!, this)
        binding.clFileLayout.visibility = View.VISIBLE
        binding.txtFileName.text = fileName
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        FilePickerUtils.handlePermissionRequestResult(requestCode, grantResults, this)
    }

    private fun createChip(text: String) {
        // Check if the item is already in the list
        if (!itemList.contains(text)) {
            itemList.add(text)
        }
        binding.chipGroup.addView(createTagChip(this, text))
    }

    private fun createTagChip(context: Context, chipName: String): Chip {
        return Chip(context).apply {
            text = chipName
            setBackgroundColor(ContextCompat.getColor(context, R.color.chipback))
            isCloseIconVisible = true
            setTextColor(ContextCompat.getColor(context, R.color.black))
            closeIcon = ContextCompat.getDrawable(applicationContext, R.drawable.cancel)
            setTextAppearance(R.style.TextTitleNormal_12sp)
            chipCornerRadius = 15f
            setPadding(24)
            setOnCloseIconClickListener {
                binding.chipGroup.removeView(this)
                val position = itemList.indexOf(text)
                itemIDList.removeAt(position)
                itemList.removeAt(position)
            }
        }
    }

    class FormData {
        private var category_id:Int? = null
        private var sub_category_id:String? = null
        private var year_of_experience:String? = null
        private var certificate:Uri? = null
        private var description:String? = null

        fun getCategoryId() = category_id
        fun getSubCategoryId() = sub_category_id
        fun getYearOfExperience() = year_of_experience
        fun getCertificate() = certificate
        fun getDescription() = description

        fun setCategoryId(id:Int) { category_id = id}
        fun setSubCategoryId(id:String)  { sub_category_id = id }
        fun setYearOfExperience(exp:String)  { year_of_experience = exp }
        fun setCertificate(uri: Uri?) {certificate = uri}
        fun setDescription(string: String) {
            description = string
        }
    }

    override fun onclick(position: Int, item: Any) {
        vm.deleteFormItem(position)
        val data = item as FormData
        binding.apply {
            vm.getSubCategory(data.getCategoryId().toString())
            for (cat in categoryList){
                if (data.getCategoryId() == cat.id){
                    binding.btnSelectExpertise.setText(cat.name)
                }
            }
            binding.edtExperiance.setText(data.getYearOfExperience().toString())
            binding.edtDescription.setText(data.getDescription())
            binding.chipGroup.removeAllViews()
            fileURI = data.getCertificate()
            addFile()
            itemIDList.clear()
            itemList.clear()
            val subCategoryArray = data.getSubCategoryId()?.split(",")
            for (subCatID in subCategoryArray!!){
                subCategoryList.clear()
                itemIDList.add(subCatID)
            }
            formData = data
        }

    }
}

