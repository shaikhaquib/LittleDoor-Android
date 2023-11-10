package com.devative.littledoor.util

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.devative.littledoor.R
import com.devative.littledoor.databinding.BottomSheetProgressBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Created by AQUIB RASHID SHAIKH on 08-11-2023.
 */
class ProgressBottomSheet (private val activity: AppCompatActivity) {
    private var count: Int = 0
    private var bottomSheetDialog: BottomSheetDialog? = null
    private lateinit var binding: BottomSheetProgressBinding

    @Synchronized
    fun show() {
        if (activity.isDestroyed) {
            return
        }
        if (count == 0) {
            bottomSheetDialog = BottomSheetDialog(activity)
            binding = BottomSheetProgressBinding.inflate(LayoutInflater.from(activity))
            bottomSheetDialog?.apply {
                setContentView(binding.root)
                setCancelable(false)
                show()
            }
        }
        count++
    }

    fun setMessage(message:String){
        binding.txtMessage.text = message
    }

    @Synchronized
    fun dismiss() {
        if (count > 0) {
            count--
            if (count == 0) {
                bottomSheetDialog?.let {
                    if (it.isShowing) {
                        bottomSheetDialog?.let {
                            it.dismiss()
                        }
                        setMessage(activity.getString(cn.pedant.SweetAlert.R.string.LOADING))
                    }
                }
            }
        }
    }

    @Synchronized
    fun reset() {
        count = 0
        bottomSheetDialog?.let {
            if (it.isShowing) {
                bottomSheetDialog?.let {
                    it.dismiss()
                }
            }
        }
    }

    @Synchronized
    fun isShowing() = count > 0
}