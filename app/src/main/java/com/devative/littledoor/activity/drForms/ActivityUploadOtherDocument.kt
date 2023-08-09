package com.devative.littledoor.activity.drForms

import FilePickerUtils.getFileNameFromUri
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.viewModels
import androidx.core.view.isGone
import com.devative.littledoor.R
import com.devative.littledoor.activity.BaseActivity
import com.devative.littledoor.adapter.FormAdapter
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DrOtherDocFormVM
import com.devative.littledoor.architecturalComponents.viewmodel.DrRegViewModel
import com.devative.littledoor.databinding.ActivityUploadOtherDocumentBinding
import com.devative.littledoor.model.DoctorDetailsResponse
import com.devative.littledoor.util.Logger
import com.devative.littledoor.util.Progress
import es.dmoral.toasty.Toasty

class ActivityUploadOtherDocument : BaseActivity(),OnClickListener,FormAdapter.FormAdapterEvent {
    lateinit var binding:ActivityUploadOtherDocumentBinding
    private val vm: DrOtherDocFormVM by viewModels()
    private val vmDR: DrRegViewModel by viewModels()
    private var certificate: Uri? = null
    private var certificateURL: String? = null
    private var formData = OtherDocumentForm()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadOtherDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        
        sutUpClick()
        initIntent()
        observe()
    }

    private fun initIntent() {
        val editPosition = intent.getIntExtra(Constants.FORM_EDIT_POSITION,-1)
        if (intent.hasExtra(Constants.FORM_EDIT_DATA)){
            val data = intent.getSerializableExtra(Constants.FORM_EDIT_DATA) as DoctorDetailsResponse.Data
            for (editData in data.other)
            {
                val formD = OtherDocumentForm()
                formD.name  = editData.name
                formD.certificateURL  = editData.document
                if ( editPosition == -1 || editData.id != data.other[editPosition].id) {
                    vm.addFormItem(formD)
                }else{
                    setDataUI(formD)
                }
            }
        }
    }

    private fun setDataUI(data: OtherDocumentForm) {
        binding.apply {
            edtName.setText(data.name)
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
        binding.apply {
            btnCreate.setOnClickListener(this@ActivityUploadOtherDocument)
            llAddMore.setOnClickListener(this@ActivityUploadOtherDocument)
            llUploadFile.setOnClickListener(this@ActivityUploadOtherDocument)
            txtRemove.setOnClickListener(this@ActivityUploadOtherDocument)
        }
    }

    class OtherDocumentForm{
        var name:String? = null
        var certificate: Uri? = null
        var certificateURL: String? = null
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
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
            binding.edtName.text.isNullOrEmpty() ->{
                Toasty.error(this,"Please enter document name").show()
            }
            certificate == null && certificateURL ==null ->{
                Toasty.error(this,"Please select file").show()
            }
            else->{
                binding.apply {
                    formData = OtherDocumentForm()
                    formData.name =  binding.edtName.text.toString()
                    formData.certificate =  certificate
                    formData.certificateURL =  certificateURL
                    vm.addFormItem(formData)
                    edtName.setText("")
                    binding.clFileLayout.visibility = View.GONE
                    certificateURL = null
                    certificate = null
                    formData = OtherDocumentForm()
                }
                if (!isAddMore)
                    uploadFormData()

            }
        }
    }

    private fun uploadFormData() {
        val list = vm.formData.value
        if (list == null) {
            Toasty.error(this, "No data found").show()
        }
        val fileMap = HashMap<String, Uri>()
        val dataMap = HashMap<String, String>()
        dataMap["step"] = "7"
        for (i in list!!.indices) {
            list[i].certificate?.let {
                fileMap["other[$i][document]"] = it
            }
            list[i].certificateURL?.let {
                dataMap["other[$i][document]"] = it
            }
            dataMap["other[$i][name]"] = list[i].name!!

        }
        Logger.d("TAG", "uploadFormData: ${dataMap.toString()}")
        vmDR.uploadData(
            this@ActivityUploadOtherDocument,
            fileMap,
            dataMap
        )

    }

    override fun onclick(position: Int, formData: Any) {
        vm.deleteFormItem(position)
        val data = formData as OtherDocumentForm
        setDataUI(data)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FilePickerUtils.PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            certificate = data?.data
            certificateURL = null
            addFile()

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

}