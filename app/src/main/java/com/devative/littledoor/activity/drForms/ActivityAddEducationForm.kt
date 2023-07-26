package com.devative.littledoor.activity.drForms

import FilePickerUtils
import FilePickerUtils.getFileNameFromUri
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.activity.viewModels
import androidx.core.view.isGone
import com.devative.littledoor.activity.BaseActivity
import com.devative.littledoor.adapter.FormAdapter
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DrEducationFormVM
import com.devative.littledoor.architecturalComponents.viewmodel.DrRegViewModel
import com.devative.littledoor.databinding.ActivityAddEducationFormBinding
import com.devative.littledoor.model.DoctorDetailsResponse
import com.devative.littledoor.util.IOSDatePickerFragment
import com.devative.littledoor.util.Logger
import com.devative.littledoor.util.Progress
import es.dmoral.toasty.Toasty

class ActivityAddEducationForm : BaseActivity(), OnClickListener, FormAdapter.FormAdapterEvent {
    lateinit var binding: ActivityAddEducationFormBinding
    private val vm: DrEducationFormVM by viewModels()
    private val vmDR: DrRegViewModel by viewModels()
    private var certificate: Uri? = null
    private var certificateURL: String? = null
    private var formData = EducationFormData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEducationFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        progress = Progress(this)
        sutUpClick()
        initIntent()
        observe()
    }

    private fun observe() {
        vm.formData.observe(this){
            binding.rvFormData.adapter = FormAdapter(this,it as ArrayList<Any>,this)
            binding.formDivider.isGone = it.isNullOrEmpty()
        }
        vmDR.uploadResponse.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress?.show()
                }

                Status.SUCCESS -> {
                    progress?.dismiss()
                    if (it.data?.status == true) {
                        Toasty.success(applicationContext, it.data.message).show()
                        finish()
                    } else {
                        Toasty.error(applicationContext, it.data!!.message).show()
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
    }


    private fun sutUpClick() {
        binding.btnStartDate.setOnClickListener(this)
        binding.btnEndDate.setOnClickListener(this)
        binding.llUploadFile.setOnClickListener(this)
        binding.txtRemove.setOnClickListener(this)
        binding.btnCreate.setOnClickListener(this)
        binding.llAddMore.setOnClickListener(this)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FilePickerUtils.PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            certificate = data?.data
            certificateURL = null
            addFile()

        }
    }

    private fun initIntent() {
        val editPosition = intent.getIntExtra(Constants.FORM_EDIT_POSITION,-1)
        if (intent.hasExtra(Constants.FORM_EDIT_DATA)){
            val data = intent.getSerializableExtra(Constants.FORM_EDIT_DATA) as DoctorDetailsResponse.Data
            for (editData in data.education)
            {
                val formD = EducationFormData()
                formD.name  = editData.name
                formD.institution_name  = editData.institution_name
                formD.field_of_study  = editData.field_of_study
                formD.start_date  = editData.start_date
                formD.end_date  = editData.end_date
                formD.description  = editData.description
                formD.certificateURL = editData.documents[0]

                if ( editPosition == -1 || editData.id != data.education[editPosition].id) {
                    vm.addFormItem(formD)
                }else{
                    setDataUI(formD)
                }
            }
        }
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

            binding.btnStartDate.id -> {
                openCalender(binding.btnStartDate)
            }

            binding.btnEndDate.id -> {
                openCalender(binding.btnEndDate)
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
            binding.llAddMore.id -> {
                validateForm(true)
            }
        }
    }

    private fun validateForm(isAddMore:Boolean) {
        when{
            binding.edtLevelOfEducation.text.isNullOrEmpty() ->{
                Toasty.error(this,"Please select your LevelOfEducation").show()
            }
            binding.edtInstituteName.text.isNullOrEmpty() ->{
                Toasty.error(this,"Please select your InstituteName").show()
            }
            binding.edtFieldOfStudy.text.isNullOrEmpty() ->{
                Toasty.error(this,"Please select your FieldOfStudy").show()
            }
            binding.btnStartDate.text.isNullOrEmpty() ->{
                Toasty.error(this,"Please select your Start Date").show()
            }
            binding.btnEndDate.text.isNullOrEmpty() ->{
                Toasty.error(this,"Please select your End Date").show()
            }
            certificate == null && certificateURL ==null ->{
                Toasty.error(this,"Please select file").show()
            }
            else->{
                binding.apply {
                    formData = EducationFormData()
                    formData.name =  binding.edtLevelOfEducation.text.toString()
                    formData.institution_name =  binding.edtInstituteName.text.toString()
                    formData.field_of_study =  binding.edtFieldOfStudy.text.toString()
                    formData.description =  binding.edtDescription.text.toString()
                    formData.end_date =  binding.btnEndDate.text.toString()
                    formData.start_date =  binding.btnStartDate.text.toString()
                    formData.certificate =  certificate
                    formData.certificateURL =  certificateURL

                    vm.addFormItem(formData)
                    edtLevelOfEducation.setText("")
                    edtInstituteName.setText("Select")
                    edtDescription.setText("")
                    binding.clFileLayout.visibility = View.GONE
                    certificateURL = null
                    certificate = null
                    formData = EducationFormData()
                }
                if (!isAddMore)
                    uploadFormData()

            }
        }
    }

    private fun openCalender(btn: Button) {
        val datePickerFragment = IOSDatePickerFragment.newInstance(0, "yyyy-MM-dd")
        datePickerFragment.setOnDateSelectedListener(object :
            IOSDatePickerFragment.OnDateSelectedListener {
            override fun onDateSelected(date: String) {
                // Handle selected date
                Logger.d("TAG", "onDateSelected: $date")
                btn.text = date
            }
        })
        datePickerFragment.show(supportFragmentManager, "date_picker")
    }
    class EducationFormData {
        var name: String? = null
        var institution_name: String? = null
        var field_of_study: String? = null
        var start_date: String? = null
        var end_date: String? = null
        var certificate: Uri? = null
        var certificateURL: String? = null
        var description: String? = null
    }

    private fun uploadFormData() {
        val list = vm.formData.value
        if (list == null) {
            Toasty.error(this, "No data found").show()
        }
        val fileMap = HashMap<String, Uri>()
        val dataMap = HashMap<String, String>()
        dataMap["step"] = "2"
        for (i in list!!.indices) {
            list[i].certificate?.let {
                fileMap["education[$i][certificate][0]"] = it
            }
            list[i].certificateURL?.let {
                dataMap["education[$i][certificate][0]"] = it
            }
            dataMap["education[$i][name]"] = list[i].name!!
            dataMap["education[$i][institution_name]"] = list[i].institution_name!!
            dataMap["education[$i][field_of_study]"] = list[i].field_of_study!!
            dataMap["education[$i][start_date]"] = list[i].start_date!!
            dataMap["education[$i][end_date]"] = list[i].end_date!!
            dataMap["education[$i][description]"] = list[i].description!!
        }
        Logger.d("TAG", "uploadFormData: ${dataMap.toString()}")
        vmDR.uploadData(
            this@ActivityAddEducationForm,
                fileMap,
                dataMap
            )

    }

    override fun onclick(position: Int, item: Any) {
        vm.deleteFormItem(position)
        val data = item as ActivityAddEducationForm.EducationFormData
        setDataUI(data)

    }

    private fun setDataUI(data: ActivityAddEducationForm.EducationFormData) {
        binding.apply {
            edtLevelOfEducation.setText(data.name)
            edtInstituteName.setText(data.institution_name)
            edtFieldOfStudy.setText(data.field_of_study)
            edtDescription.setText(data.description)
            btnStartDate.setText(data.start_date)
            btnEndDate.setText(data.end_date)
            if (data.certificate != null)
                certificate = data.certificate
            if (data.certificateURL != null)
                certificateURL = data.certificateURL
            addFile()
            formData = data
        }
    }

    private fun addFile() {
        if (certificate != null) {
            val fileName = getFileNameFromUri(certificate!!, this)
            val path = FilePickerUtils.getFullPathFromUri(certificate!!, this)
            binding.clFileLayout.visibility = View.VISIBLE
            binding.txtFileName.text = fileName
        }else if (certificateURL != null){
            val fileName = certificateURL?.substring(certificateURL!!.lastIndexOf("/")+1)
            binding.clFileLayout.visibility = View.VISIBLE
            binding.txtFileName.text = fileName
        }
    }


}