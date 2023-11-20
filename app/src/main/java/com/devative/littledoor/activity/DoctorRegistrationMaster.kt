package com.devative.littledoor.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.devative.littledoor.R
import com.devative.littledoor.activity.drForms.ActivityAddAddress
import com.devative.littledoor.activity.drForms.ActivityAddAppreciation
import com.devative.littledoor.activity.drForms.ActivityAddEducationForm
import com.devative.littledoor.activity.drForms.ActivityAddExperience
import com.devative.littledoor.activity.drForms.ActivityAddExpertise
import com.devative.littledoor.activity.drForms.ActivityLanguageSelection
import com.devative.littledoor.activity.drForms.ActivityUploadOtherDocument
import com.devative.littledoor.adapter.DoctorFormMasterAdapter
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DrRegViewModel
import com.devative.littledoor.databinding.ActivityDoctorRegistrationMasterBinding
import com.devative.littledoor.model.DoctorDetailsResponse
import com.devative.littledoor.model.DrRegistrationMasterModel
import com.devative.littledoor.util.ListSpacingDecoration
import com.devative.littledoor.util.Progress
import com.devative.littledoor.util.Utility
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class DoctorRegistrationMaster : BaseActivity(), DoctorFormMasterAdapter.FormMasterEvent {
    lateinit var binding: ActivityDoctorRegistrationMasterBinding
    val formMasterList = ArrayList<DrRegistrationMasterModel>()
    lateinit var adapter: DoctorFormMasterAdapter
    private var doctorDetails:DoctorDetailsResponse?= null
    private val vm: DrRegViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.overlays_purple)
        }
        binding = ActivityDoctorRegistrationMasterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = DoctorFormMasterAdapter(this,formMasterList,doctorDetails,this)
        adapter.setHasStableIds(true)
        binding.rvFormMaster.adapter = adapter
        binding.rvFormMaster.addItemDecoration(ListSpacingDecoration())
        binding.txtName.text = "${getString(R.string.hello)}"
        formMasterList.apply {
            add(DrRegistrationMasterModel("Work experience"))
            add(DrRegistrationMasterModel("Education"))
            add(DrRegistrationMasterModel("Expertise"))
            add(DrRegistrationMasterModel("Address"))
            add(DrRegistrationMasterModel("Language"))
            add(DrRegistrationMasterModel("Appreciation"))
            add(DrRegistrationMasterModel("Other documents"))
        }
        adapter.notifyDataSetChanged()
        binding.btnFinish.setOnClickListener {
            if (intent.hasExtra(Constants.TH_REGISTERED)){
                finish()
            }else  if (doctorDetails?.data?.form_status == 1) {
                Utility.savePrefBoolean(applicationContext, Constants.IS_DR_Reg_Finish, true)
                startActivity(
                    Intent(
                        applicationContext,
                        ProfilePicUploadActivity::class.java
                    ).putExtra(Constants.IS_DOCTOR, true)
                )

            }else{
                finish()
            }
        }
       observe()
        if (intent.hasExtra(Constants.TH_REGISTERED)){
            binding.btnFinish.text = "Back"
        }
    }

    override fun onResume() {
        super.onResume()
        vm.getDoctorDetails()
    }
    fun observe(){

        vm.doctorDetailsData.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    if (progress?.isShowing() == false)
                    progress?.show()
                }

                Status.SUCCESS -> {
                    progress?.dismiss()
                    if (it.data?.status == true) {
                        binding.txtName.text = "${getString(R.string.hello)} ${it.data.data.first_name}"
                        doctorDetails = it.data
                        adapter.setBindFormData(doctorDetails)
                        if (doctorDetails?.data?.form_status != 1){
                            binding.btnFinish.isEnabled = false
                        }
                      //  Toasty.success(applicationContext, it.data.message).show()
                    } else {
                      //  Toasty.error(applicationContext, it.data!!.message).show()
                    }
                }

                Status.ERROR -> {
                    progress?.dismiss()
/*
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
*/
                }

            }
        }
        vm.uploadResponse.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress?.show()
                }

                Status.SUCCESS -> {
                    vm.getDoctorDetails()
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


    override fun onClick(position: Int) {
    }

    override fun onClickAdd(position: Int) {
        when(position)
        {
            0 -> startActivityForEdit(-1,ActivityAddExperience::class.java)
            1 -> startActivityForEdit(-1,ActivityAddEducationForm::class.java)
            2 -> startActivityForEdit(-1,ActivityAddExpertise::class.java)
            3 -> startActivityForEdit(-1,ActivityAddAddress::class.java)
            4 -> startActivity(Intent(applicationContext,ActivityLanguageSelection::class.java))
            5 -> startActivityForEdit(position,ActivityAddAppreciation::class.java)
            6 -> startActivityForEdit(position,ActivityUploadOtherDocument::class.java)
        }
    }

    override fun onEdit(type: Any, position: Int) {
        when(type){
            is DoctorDetailsResponse.Data.WorkExperience->{
                startActivityForEdit(position,ActivityAddExperience::class.java)
            }
            is DoctorDetailsResponse.Data.Education->{
                startActivityForEdit(position,ActivityAddEducationForm::class.java)
            }
            is DoctorDetailsResponse.Data.Addres->{
                startActivityForEdit(position,ActivityAddAddress::class.java)
            }
            is DoctorDetailsResponse.Data.Appreciation->{
                startActivityForEdit(position,ActivityAddAppreciation::class.java)
            }
            is DoctorDetailsResponse.Data.Other->{
                startActivityForEdit(position,ActivityUploadOtherDocument::class.java)
            }
        }
    }

    override fun onExpertiseRemove(list: ArrayList<String>) {
        if (list.isNotEmpty()) {
            val fileMap = HashMap<String, Uri>()
            val dataMap = HashMap<String, String>()
            dataMap["step"] = "3"
            for (i in list!!.indices) {
                dataMap["skills[$i]"] = list[i]
            }
            vm.uploadData(
                this,
                fileMap,
                dataMap
            )
        }else{
            Toasty.info(this,"Please add additional skill/expertise before eliminating everything!").show()
            binding.rvFormMaster.adapter?.notifyDataSetChanged()
        }
    }
    override fun onLangRemove(list: ArrayList<String>) {
        if (list.isNotEmpty()) {
            val fileMap = HashMap<String, Uri>()
            val dataMap = HashMap<String, String>()
            dataMap["step"] = "5"
            for (i in list!!.indices) {
                dataMap["languages[i]"] = list[i]
            }
            vm.uploadData(
                this,
                fileMap,
                dataMap
            )
        }else{
            Toasty.info(this,"Please add additional language before eliminating everything!").show()
            binding.rvFormMaster.adapter?.notifyDataSetChanged()
        }
    }

    private fun startActivityForEdit(position: Int, activity: Class<*>) {
        startActivity(
            Intent(applicationContext, activity).apply {
                putExtra(Constants.FORM_EDIT_POSITION, position)
                vm.doctorDetailsData.value?.data?.data?.let {
                    putExtra(Constants.FORM_EDIT_DATA, it)
                }
            }

        )
    }
}