package com.devative.littledoor.activity.drForms

import FilePickerUtils.getFileNameFromUri
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.devative.littledoor.activity.BaseActivity
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DrRegViewModel
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityAddAddressBinding
import com.devative.littledoor.model.DoctorDetailsResponse
import com.devative.littledoor.model.GetAllCitiesResponse
import com.devative.littledoor.model.SearchAbleList
import com.devative.littledoor.util.Logger
import com.devative.littledoor.util.Progress
import com.devative.littledoor.util.SingleSelectBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class ActivityAddAddress : BaseActivity(), View.OnClickListener {
    private var selectedCityId = -1
    private var selectedStateID = -1
    private val cityStateList = ArrayList<GetAllCitiesResponse.Data>()
    private val drVM: DrRegViewModel by viewModels()
    private val mVm: MainViewModel by viewModels()
    private lateinit var binding: ActivityAddAddressBinding
    private var certificate: Uri? = null
    private var certificateURL: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        
        clickListener()
        initIntent()
        observe()
    }

    private fun initIntent() {
        if (intent.hasExtra(Constants.FORM_EDIT_DATA)) {
            val data =
                intent.getSerializableExtra(Constants.FORM_EDIT_DATA) as DoctorDetailsResponse.Data
            if (data.address.isNotEmpty()) {
                val editData = data.address[0]
                binding.apply {
                    edtAddLine1.setText(editData.address_line_1)
                    edtAddLine2.setText(editData.address_line_2)
                    edtZipcode.setText(editData.pincode)
                    btnCitySelect.setText(editData.city_name)
                    btnSelectState.setText(editData.state_name)
                    selectedCityId = editData.city_id
                    selectedStateID = editData.city_id
                    certificateURL = data.address_proof_url
                    addFile()
                }
            }
        }
    }

    private fun clickListener() {
        binding.btnSelectState.setOnClickListener(this)
        binding.btnAddressType.setOnClickListener(this)
        binding.btnCitySelect.setOnClickListener(this)
        binding.llUploadFile.setOnClickListener(this)
        binding.btnCreate.setOnClickListener(this)
        binding.txtRemove.setOnClickListener(this)
    }

    private fun observe() {
        mVm.getCities()
        mVm.getCities.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress?.show()
                }

                Status.SUCCESS -> {
                    progress?.dismiss()
                    if (it.data?.status == true) {
                        cityStateList.addAll(it.data.data)
                    } else {
                        Toasty.error(applicationContext, "No data found").show()
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
        drVM.uploadResponse.observe(this) {
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
            binding.btnSelectState.id -> {
                val items = ArrayList<SearchAbleList>()
                val uniqueStates =
                    cityStateList.distinctBy { it.state_name }.sortedBy { it.state_name }
                for (i in uniqueStates) {
                    if (i.state_id != null && i.state_name != null)
                        items.add(SearchAbleList(i.state_id, i.state_name))
                }
                val dialog = SingleSelectBottomSheetDialogFragment(
                    this,
                    items,
                    "Select State",
                    0,
                    true
                ) { selectedValue ->
                    Logger.d("TAG", "onCreate: $selectedValue")
                    binding.btnSelectState.text = selectedValue.title
                    binding.btnCitySelect.text = ""
                    selectedStateID = selectedValue.position
                    selectedCityId = -1
                }
                dialog.show(supportFragmentManager, "SingleSelectBottomSheetDialogFragment")
            }

            binding.btnCitySelect.id -> {
                if (selectedStateID == -1) {
                    Toasty.info(applicationContext, "Please select state").show()
                    return
                }
                val items = ArrayList<SearchAbleList>()
                val sortedAndFiltered =
                    cityStateList.sortedBy { it.state_id }.filter { it.state_id == selectedStateID }
                for (i in sortedAndFiltered) {
                    if (i.id != null && i.city_name != null)
                        items.add(SearchAbleList(i.id, i.city_name))
                }
                val dialog = SingleSelectBottomSheetDialogFragment(
                    this,
                    items,
                    "Select City",
                    0,
                    true
                ) { selectedValue ->
                    Logger.d("TAG", "onCreate: $selectedValue")
                    binding.btnCitySelect.text = selectedValue.title
                    selectedCityId = selectedValue.position
                }
                dialog.show(supportFragmentManager, "SingleSelectBottomSheetDialogFragment")
            }

            binding.btnAddressType.id -> {
                val items = arrayListOf<SearchAbleList>(SearchAbleList(0,"Home",),SearchAbleList(1,"Office",))
                val dialog = SingleSelectBottomSheetDialogFragment(
                    this,
                    items,
                    "Select address type",
                    0,
                    false
                ) { selectedValue ->
                    binding.btnAddressType.text = selectedValue.title
                }
                dialog.show(supportFragmentManager, "SingleSelectBottomSheetDialogFragment")
            }
            binding.llUploadFile.id -> {
                FilePickerUtils.pickFileFromFileManager(this)
            }

            binding.txtRemove.id -> {
                binding.clFileLayout.visibility = View.GONE
            }

            binding.btnCreate.id -> {
                validateForm()
            }

        }
    }

    private fun validateForm() {
        if (binding.edtAddLine1.text.isEmpty()) {
            Toasty.error(applicationContext, "Please enter Address line 1").show()
        } else if (binding.edtAddLine2.text.isEmpty()) {
            Toasty.error(applicationContext, "Please enter Address line 2").show()
        } else if (binding.edtZipcode.text.isEmpty()) {
            Toasty.error(applicationContext, "Please enter zipcode").show()
        } else if (selectedStateID == -1) {
            Toasty.error(applicationContext, "Please select state").show()
        } else if (selectedCityId == -1) {
            Toasty.error(applicationContext, "Please select city").show()
        } else if (certificate == null && certificateURL == null) {
            Toasty.error(applicationContext, "Please select city").show()
        } else {
            setUpload()
        }
    }

    private fun setUpload() {
        val fileMap = HashMap<String, Uri>()
        val dataMap = HashMap<String, String>()
        dataMap["step"] = "4"
        certificate?.let {
            fileMap["address_proof_document"] = it
        }
        certificateURL?.let {
            dataMap["address_proof_document"] = it
        }
        dataMap["address[0][address_line_1]"] = binding.edtAddLine1.text.toString()
        dataMap["address[0][address_line_2]"] = binding.edtAddLine2.text.toString()
        dataMap["address[0][pincode]"] = binding.edtZipcode.text.toString()
        dataMap["address[0][city_id]"] = selectedCityId.toString()
        dataMap["address[0][state_id]"] = selectedStateID.toString()
        dataMap["address[0][address_type]"] =binding.btnAddressType.text.toString()

        Logger.d("TAG", "uploadFormData: ${dataMap.toString()}")
        drVM.uploadData(
            this@ActivityAddAddress,
            fileMap,
            dataMap
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FilePickerUtils.PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            certificate = data?.data
            certificateURL = null
            addFile()
        }
    }

    private fun addFile() {
        if (certificate != null) {
            val fileName = getFileNameFromUri(certificate!!, this)
            val path = FilePickerUtils.getFullPathFromUri(certificate!!, this)
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

}