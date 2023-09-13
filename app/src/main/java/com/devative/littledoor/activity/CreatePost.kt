package com.devative.littledoor.activity

import FilePickerUtils.getRecentPicturesFilePathsFromGallery
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.apicall.APIClient
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DrRegViewModel
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityUploadPostBinding
import com.devative.littledoor.util.ImagePickerDialog
import com.devative.littledoor.util.Utility
import es.dmoral.toasty.Toasty


class CreatePost : BaseActivity() {

    private val binding by lazy {
        ActivityUploadPostBinding.inflate(layoutInflater)
    }
    private var uri: Uri? = null
    private val viewModel: MainViewModel by viewModels()
    private val vmDR: DrRegViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.fetchUserData()
        viewModel.basicDetails.observe(this) {
            if (!it.isNullOrEmpty()) {
                basicDetails = it[0]
                binding.imgProfile.load(basicDetails?.image_url?:"", R.drawable.profile_placeholder)
            }
        }
        binding.imgCamera.setOnClickListener {
            val dialog = ImagePickerDialog(
                this,
                launcher
            )
            dialog.show(supportFragmentManager, "ImagePickerDialog")
        }
        binding.btnPost.setOnClickListener {
          uploadPost()
        }
        binding.txtCancel.setOnClickListener {
          finish()
        }
        observe()
    }

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            uri = it.data?.data
            uri?.let {
                binding.imgPost.visibility = View.VISIBLE
                binding.imgPost.setImageURI(uri)
            }
        }
    }

    private fun uploadPost() {

        if (uri == null && binding.txtContent.text.isNullOrEmpty()) {
            Utility.errorToast(
                applicationContext,
                "You cannot have a blank post, Please provide some text or an image."
            )
            return
        }

        val fileMap = HashMap<String, Uri>()
        val dataMap = HashMap<String, String>()
        uri?.let {
            fileMap["image"] = it
        }
        if (!binding.txtContent.text.isNullOrEmpty()) {
            dataMap["post"] = binding.txtContent.text.toString().trim()
        }
        vmDR.uploadData(
            this@CreatePost,
            fileMap,
            dataMap,
            APIClient.CREATE_POST
        )
    }

    fun observe(){
        vmDR.uploadResponse.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        Toasty.success(applicationContext, it.data.message).show()
                        finish()
                    } else {
                        Toasty.error(applicationContext, it.data!!.message).show()
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

    }


}