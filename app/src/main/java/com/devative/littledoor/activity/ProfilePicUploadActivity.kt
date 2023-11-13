package com.devative.littledoor.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.apicall.APIClient
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DrRegViewModel
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityProfilePicUploadBinding
import com.nareshchocha.filepickerlibrary.models.ImageCaptureConfig
import com.nareshchocha.filepickerlibrary.models.PickMediaConfig
import com.nareshchocha.filepickerlibrary.ui.FilePicker
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class ProfilePicUploadActivity : BaseActivity(), OnClickListener {
   private val binding: ActivityProfilePicUploadBinding by lazy {
        ActivityProfilePicUploadBinding.inflate(layoutInflater)
    }
    private val isDoctor:Boolean by lazy {
        intent.hasExtra(Constants.IS_DOCTOR)
    }
    private var uri:Uri? = null
    private val viewModel: MainViewModel by viewModels()
    private val vmDR: DrRegViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        observer()
        binding.apply {
            lilCamera.setOnClickListener(this@ProfilePicUploadActivity)
            lilGallery.setOnClickListener(this@ProfilePicUploadActivity)
            btnSubmit.setOnClickListener(this@ProfilePicUploadActivity)
            txtSkip.setOnClickListener(this@ProfilePicUploadActivity)
            //txtSkip.isVisible = isDoctor
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.lilCamera.id -> openCamera()
            binding.lilGallery.id -> openGallery()
            binding.btnSubmit.id -> submit()
            binding.txtSkip.id ->   startActivity()

        }
    }

    private fun submit() {
        if (uri == null){
            Toasty.success(applicationContext,"Please select image..").show()
        }else {
            updateData()
        }
    }

    fun observer(){
        vmDR.uploadResponse.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress?.show()
                }

                Status.SUCCESS -> {
                    progress?.dismiss()
                    it.data?.let { it1 -> Toasty.success(applicationContext, it1.message).show() }
                    viewModel.getUserDetails()
                    Handler(Looper.getMainLooper()).postDelayed({startActivity()},500)
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

    private fun startActivity() {
        if (isDoctor) {
            startActivity(
                Intent(
                    applicationContext,
                    MainActivity::class.java
                ).putExtra(Constants.IS_DOCTOR, true)
            )
        } else {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
        finish()
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

    private fun updateData() {
        val fileMap = HashMap<String, Uri>()
        val dataMap = HashMap<String, String>()
        uri?.let {
            fileMap["image"] = it
        }
        vmDR.uploadData(
            this@ProfilePicUploadActivity,
            fileMap,
            dataMap,
            APIClient.UPDATE_PROFILE
        )
    }


}