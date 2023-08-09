package com.devative.littledoor.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.databinding.ActivityProfilePicUploadBinding
import com.nareshchocha.filepickerlibrary.models.ImageCaptureConfig
import com.nareshchocha.filepickerlibrary.models.PickMediaConfig
import com.nareshchocha.filepickerlibrary.ui.FilePicker
import es.dmoral.toasty.Toasty

class ProfilePicUploadActivity : AppCompatActivity(), OnClickListener {
   private val binding: ActivityProfilePicUploadBinding by lazy {
        ActivityProfilePicUploadBinding.inflate(layoutInflater)
    }
    private val isDoctor:Boolean by lazy {
        intent.hasExtra(Constants.IS_DOCTOR)
    }
    private var uri:Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.apply {
            lilCamera.setOnClickListener(this@ProfilePicUploadActivity)
            lilGallery.setOnClickListener(this@ProfilePicUploadActivity)
            btnSubmit.setOnClickListener(this@ProfilePicUploadActivity)
            txtSkip.setOnClickListener(this@ProfilePicUploadActivity)
            txtSkip.isVisible = isDoctor
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.lilCamera.id -> openCamera()
            binding.lilGallery.id -> openGallery()
            binding.btnSubmit.id -> submit()
            binding.txtSkip.id ->   startActivity(Intent(applicationContext, MainActivity::class.java).putExtra(Constants.IS_DOCTOR,true))

        }
    }

    private fun submit() {
        if (uri == null){
            Toasty.success(applicationContext,"Please select image..").show()
        }else if (isDoctor){
            startActivity(Intent(applicationContext, MainActivity::class.java).putExtra(Constants.IS_DOCTOR,true))
            finish()
        }else{
            Toasty.success(applicationContext,"Profile pic uploaded..").show()
            finish()
        }
    }

    private fun openGallery() {
        launcher.launch(
            FilePicker.Builder(this)
                .pickMediaBuild(
                    PickMediaConfig
                        (
                        popUpIcon = R.drawable.ic_gallery,// DrawableRes Id
                        popUpText = "Gallery",
                        maxFiles = 1,// max files working only in android latest v
                    )
                )
        )
    }

    private fun openCamera() {
        launcher.launch(
            FilePicker.Builder(this)
                .imageCaptureBuild(ImageCaptureConfig())
        )
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

}