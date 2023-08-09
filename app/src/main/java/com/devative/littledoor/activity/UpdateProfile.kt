package com.devative.littledoor.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.apicall.APIClient
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DrRegViewModel
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityUpdateProfileBinding
import com.devative.littledoor.model.GetAllCitiesResponse
import com.devative.littledoor.model.UserDetails
import com.devative.littledoor.util.ImagePickerDialog
import com.devative.littledoor.util.Logger
import es.dmoral.toasty.Toasty

class UpdateProfile : BaseActivity(),View.OnClickListener {
    val binding: ActivityUpdateProfileBinding by lazy {
        ActivityUpdateProfileBinding.inflate(layoutInflater)
    }
    private var selectedCityId = -1
    private var selectedDate = ""
    private var selectedGender = ""
    private val citiesList = ArrayList<GetAllCitiesResponse.Data>()
    private val viewModel:MainViewModel by viewModels()
    private val vmDR: DrRegViewModel by viewModels()
    private var uri: Uri? = null
    private var isNameUpdated = false
    private var isEmailUpdate = false
    private var userData: UserDetails.Data? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        init()
        setViewModel()
        updateButtonStatus()
    }


    private fun init() {
        binding.btnCreate.setOnClickListener(this)
        binding.btnCamera.setOnClickListener(this)
        binding.edtName.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isNameUpdated = !userData?.name.equals(p0.toString())
                updateButtonStatus()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.edtEmail.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isEmailUpdate = !userData?.email.equals(p0.toString())
                updateButtonStatus()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun updateButtonStatus() {
            binding.btnCreate.apply {
                if (uri != null || isEmailUpdate || isNameUpdated) {
                    backgroundTintList = ContextCompat.getColorStateList(applicationContext, R.color.primary)
                    isEnabled = true
                }else{
                    backgroundTintList = ContextCompat.getColorStateList(applicationContext, carbon.R.color.carbon_grey_300)
                    isEnabled = false
                }
        }
    }

    private fun setViewModel() {
        viewModel.getUserDetails()
        viewModel.userDetails.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        it.data.let { data->
                            data.data.apply {
                                userData = this
                                binding.edtName.setText(name)
                                binding.edtEmail.setText(email)
                                binding.btnCitySelect.setText(city_name)
                                binding.btnGender.setText(gender)
                                selectedCityId = city_id
                                binding.btnCalender.setText(dob)
                                image_url?.let { it1 -> binding.imgProfile.load(it1,R.drawable.profile_view)}
                                binding.imgProfile.borderColor =
                                    ContextCompat.getColor(applicationContext, R.color.grey_primary)
                                binding.imgProfile.borderWidth = 10
                            }

                        }
                    } else {
                        Toasty.error(applicationContext,getString(R.string.some_thing_went_wrong)).show()
                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
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
            binding.btnCamera.id -> openGallery()
            binding.btnCreate.id -> {
                updateData()
            }
        }
    }

    private fun updateData() {
            val fileMap = HashMap<String, Uri>()
            val dataMap = HashMap<String, String>()
            uri?.let {
                fileMap["image"] = it
            }
            if (isNameUpdated){
                if (binding.edtName.text.isEmpty()){
                    binding.edtName.error = "You cannot keep your name empty"
                    return
                }else{
                    dataMap["name"] = binding.edtName.text.toString().trim()
                }
            }
            if (isEmailUpdate){
                if (binding.edtEmail.text.isEmpty()){
                    binding.edtEmail.error = "You cannot keep your email empty"
                    return
                }else if(!isValidEmail(binding.edtEmail.text.toString())){
                    binding.edtEmail.error = "Please enter valid email"
                    return
                }else{
                    dataMap["email"] = binding.edtEmail.text.toString().trim()
                }
            }
            vmDR.uploadData(
                this@UpdateProfile,
                fileMap,
                dataMap,
                APIClient.UPDATE_PROFILE
            )
    }

    private fun openGallery() {
        val dialog = ImagePickerDialog(
            this,
            launcher
        )
        dialog.show(supportFragmentManager, "ImagePickerDialog")

    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$")
        return email.matches(emailRegex)
    }
    val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                uri = it.data?.data
                uri?.let {
                    binding.imgProfile.setImageURI(uri)
                    binding.imgProfile.borderColor =
                        ContextCompat.getColor(applicationContext, R.color.grey_primary)
                    binding.imgProfile.borderWidth = 10
                    updateButtonStatus()
                }
            }
        }

}