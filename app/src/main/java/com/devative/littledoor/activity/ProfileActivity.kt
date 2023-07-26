package com.devative.littledoor.activity

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.devative.littledoor.R
import com.devative.littledoor.databinding.ActivityProfileBinding
import com.nareshchocha.filepickerlibrary.models.ImageAndVideo
import com.nareshchocha.filepickerlibrary.models.ImageCaptureConfig
import com.nareshchocha.filepickerlibrary.models.ImageOnly
import com.nareshchocha.filepickerlibrary.models.PickMediaConfig
import com.nareshchocha.filepickerlibrary.ui.FilePicker
import com.nareshchocha.filepickerlibrary.utilities.appConst.Const

class ProfileActivity : AppCompatActivity(), OnClickListener {
    lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            lilCamera.setOnClickListener(this@ProfileActivity)
            lilGallery.setOnClickListener(this@ProfileActivity)
            btnSubmit.setOnClickListener(this@ProfileActivity)
            txtSkip.setOnClickListener(this@ProfileActivity)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.lilCamera.id -> {
                launcher.launch(
                    FilePicker.Builder(this)
                        .imageCaptureBuild(ImageCaptureConfig())
                )
            }

            binding.lilGallery.id -> {
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
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data
                uri?.let {
                    binding.imgProfile.setImageURI(uri)
                    binding.imgProfile.borderColor =
                        ContextCompat.getColor(applicationContext, R.color.grey_primary)
                    binding.imgProfile.borderWidth = 10
                }
            }
        }
}