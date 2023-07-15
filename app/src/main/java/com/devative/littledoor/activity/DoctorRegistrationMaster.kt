package com.devative.littledoor.activity

import android.content.Intent
import android.hardware.SensorPrivacyManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContentProviderCompat.requireContext
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
import com.devative.littledoor.databinding.ActivityDoctorRegistrationMasterBinding
import com.devative.littledoor.model.DrRegistrationMasterModel

class DoctorRegistrationMaster : AppCompatActivity(), DoctorFormMasterAdapter.FormMasterEvent {
    lateinit var binding: ActivityDoctorRegistrationMasterBinding
    val formMasterList = ArrayList<DrRegistrationMasterModel>()
    lateinit var adapter: DoctorFormMasterAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.overlays_purple)
        }
        binding = ActivityDoctorRegistrationMasterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = DoctorFormMasterAdapter(this,formMasterList,this)
        binding.rvFormMaster.adapter = adapter
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
        
    }


    override fun onClick(position: Int) {
    }

    override fun onClickAdd(position: Int) {
        when(position)
        {
            0 -> startActivity(Intent(applicationContext,ActivityAddExperience::class.java))
            1 -> startActivity(Intent(applicationContext,ActivityAddEducationForm::class.java))
            2 -> startActivity(Intent(applicationContext,ActivityAddExpertise::class.java))
            3 -> startActivity(Intent(applicationContext,ActivityAddAddress::class.java))
            4 -> startActivity(Intent(applicationContext,ActivityLanguageSelection::class.java))
            5 -> startActivity(Intent(applicationContext,ActivityAddAppreciation::class.java))
            6 -> startActivity(Intent(applicationContext,ActivityUploadOtherDocument::class.java))
        }
    }
}