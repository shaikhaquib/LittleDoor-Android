package com.devative.littledoor.activity.drForms

import FilePickerUtils.getFileNameFromUri
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.core.view.isGone
import com.devative.littledoor.R
import com.devative.littledoor.activity.BaseActivity
import com.devative.littledoor.adapter.FormAdapter
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DrAppreciationFormVM
import com.devative.littledoor.architecturalComponents.viewmodel.DrRegViewModel
import com.devative.littledoor.databinding.ActivityAddAppreciationBinding
import com.devative.littledoor.model.DoctorDetailsResponse
import com.devative.littledoor.util.IOSDatePickerFragment
import com.devative.littledoor.util.Logger
import com.devative.littledoor.util.Progress
import es.dmoral.toasty.Toasty

class ActivityAddAppreciation : BaseActivity(), View.OnClickListener, FormAdapter.FormAdapterEvent {
    lateinit var binding: ActivityAddAppreciationBinding
    private val vmDR: DrRegViewModel by viewModels()
    private val vm: DrAppreciationFormVM by viewModels()
    private var certificate: Uri? = null
    private var certificateURL: String? = null
    private var formData = AppreciationForm()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAppreciationBinding.inflate(layoutInflater)
        
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        initIntent()
        observe()
        handleCLick()
    }

    private fun handleCLick() {
        binding.apply {
            btnCreate.setOnClickListener(this@ActivityAddAppreciation)
            btnIssuedDate.setOnClickListener(this@ActivityAddAppreciation)
            liUploadFile.setOnClickListener(this@ActivityAddAppreciation)
            txtRemove.setOnClickListener(this@ActivityAddAppreciation)
        }
    }

    private fun initIntent() {
        val editPosition = intent.getIntExtra(Constants.FORM_EDIT_POSITION, -1)
        if (intent.hasExtra(Constants.FORM_EDIT_DATA)) {
            val data =
                intent.getSerializableExtra(Constants.FORM_EDIT_DATA) as DoctorDetailsResponse.Data
            for (editData in data.appreciation) {
                val formD = AppreciationForm()
                formD.name = editData.name
                formD.category_achieved = editData.category_achieved
                formD.issue_date = editData.issue_date
                formD.description = editData.description
                formD.certificateURL = editData.image_url
                if (editPosition == -1 || editData.id != data.appreciation[editPosition].id) {
                    vm.addFormItem(formD)
                } else {
                    setDataUI(formD)
                }
            }
        }
    }

    private fun setDataUI(data: AppreciationForm) {
        binding.apply {
            edtName.setText(data.name)
            edtCategory.setText(data.category_achieved)
            btnIssuedDate.setText(data.issue_date)
            edtDescription.setText(data.description)
            if (data.certificate != null)
                certificate = data.certificate
            if (data.certificateURL != null)
                certificateURL = data.certificateURL
            addFile()
            formData = data
        }
    }

    class AppreciationForm {
        var name: String? = null
        var category_achieved: String? = null
        var issue_date: String? = null
        var description: String? = null
        var certificate: Uri? = null
        var certificateURL: String? = null
    }

    private fun addFile() {
        if (certificate != null) {
            val fileName = getFileNameFromUri(certificate!!, this)
            binding.clFileLayout.visibility = View.VISIBLE
            binding.txtFileName.text = fileName
        } else if (certificateURL != null) {
            val fileName = certificateURL?.substring(certificateURL!!.lastIndexOf("/") + 1)
            binding.clFileLayout.visibility = View.VISIBLE
            binding.txtFileName.text = fileName
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FilePickerUtils.PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            certificate = data?.data
            certificateURL = null
            addFile()
        }
    }

    private fun observe() {
        vm.formData.observe(this) {
            binding.rvFormData.adapter = FormAdapter(this, it as ArrayList<Any>, this)
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


    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.btnIssuedDate.id -> {
                openCalender(binding.btnIssuedDate)
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

            binding.liUploadFile.id -> {
                FilePickerUtils.pickFileFromFileManager(this)
            }
        }
    }

    private fun validateForm(isAddMore: Boolean) {
        when {
            binding.edtName.text.isNullOrEmpty() -> {
                Toasty.error(this, "Please enter award name").show()
            }

            binding.edtCategory.text.isNullOrEmpty() -> {
                Toasty.error(this, "Please enter ${binding.edtCategory.hint}").show()
            }

            binding.btnIssuedDate.text.isNullOrEmpty() -> {
                Toasty.error(this, "Please enter ${binding.btnIssuedDate.hint}").show()
            }

            certificate == null && certificateURL == null -> {
                Toasty.error(this, "Please select file").show()
            }

            binding.edtDescription.text.isNullOrEmpty() -> {
                Toasty.error(this, "Please enter ${binding.edtDescription.hint}").show()
            }

            else -> {
                binding.apply {
                    formData = AppreciationForm()
                    formData.name = binding.edtName.text.toString()
                    formData.category_achieved = binding.edtCategory.text.toString()
                    formData.issue_date = binding.btnIssuedDate.text.toString()
                    formData.description = binding.edtDescription.text.toString()
                    formData.certificate = certificate
                    formData.certificateURL = certificateURL

                    vm.addFormItem(formData)
                    binding.edtName.setText("")
                    binding.edtCategory.setText("")
                    binding.btnIssuedDate.text = ""
                    edtDescription.setText("")
                    binding.clFileLayout.visibility = View.GONE
                    certificateURL = null
                    certificate = null
                    formData = AppreciationForm()
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
         dataMap["step"] = "6"
        for (i in list!!.indices) {
            list[i].certificate?.let {
                fileMap["appreciation[$i][image]"] = it
            }
            list[i].certificateURL?.let {
                dataMap["appreciation[$i][image]"] = it
            }
            dataMap["appreciation[$i][name]"] = list[i].name!!
            dataMap["appreciation[$i][category_achieved]"] = list[i].category_achieved!!
            dataMap["appreciation[$i][issue_date]"] = list[i].issue_date!!
            dataMap["appreciation[$i][description]"] = list[i].description!!
        }
        Logger.d("TAG", "uploadFormData: ${dataMap.toString()}")
        vmDR.uploadData(
            this@ActivityAddAppreciation,
            fileMap,
            dataMap
        )

    }


    private fun openCalender(btn: Button) {
        val datePickerFragment = IOSDatePickerFragment.newInstance(0, "yyyy-MM-dd")
        datePickerFragment.setOnDateSelectedListener(object :
            IOSDatePickerFragment.OnDateSelectedListener {
            override fun onDateSelected(date: String) {
                Logger.d("TAG", "onDateSelected: $date")
                btn.text = date
            }
        })
        datePickerFragment.show(supportFragmentManager, "date_picker")
    }

    override fun onclick(position: Int, formData: Any) {
        vm.deleteFormItem(position)
        val data = formData as AppreciationForm
        setDataUI(data)
    }
}