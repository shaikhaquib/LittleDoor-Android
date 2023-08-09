package com.devative.littledoor.util

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.devative.littledoor.R
import com.devative.littledoor.databinding.ImagePickerSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nareshchocha.filepickerlibrary.models.ImageCaptureConfig
import com.nareshchocha.filepickerlibrary.models.PickMediaConfig
import com.nareshchocha.filepickerlibrary.ui.FilePicker


/**
 * Created by AQUIB RASHID SHAIKH on 11-03-2023.
 */


class ImagePickerDialog(
    private val context: AppCompatActivity,
    private val launcher: ActivityResultLauncher<Intent>
) : BottomSheetDialogFragment(),OnClickListener {


    private lateinit var binding:ImagePickerSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ImagePickerSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        isCancelable = false
        binding.lilCamera.setOnClickListener(this)
        binding.lilGallery.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }


    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (getContext() as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }




    private fun openGallery() {
        launcher.launch(
            FilePicker.Builder(context)
                .pickMediaBuild(
                    PickMediaConfig
                        (
                        popUpIcon = R.drawable.ic_gallery,// DrawableRes Id
                        popUpText = "Gallery",
                        maxFiles = 1,// max files working only in android latest v
                    )
                )
        )
        dismiss()
    }

    private fun openCamera() {
        launcher.launch(
            FilePicker.Builder(context)
                .imageCaptureBuild(ImageCaptureConfig())
        )
        dismiss()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.lilCamera.id -> openCamera()
            binding.lilGallery.id -> openGallery()
            binding.btnSubmit.id -> dismiss()
        }

    }

}
