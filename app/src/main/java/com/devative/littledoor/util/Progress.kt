package com.devative.littledoor.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.devative.littledoor.R

class Progress(private val activity: AppCompatActivity) {
    private var count: Int = 0
    private var dialog: Dialog? = null

    @Synchronized
    fun show() {
        if (activity.isDestroyed) {
            return
        }
        if (count == 0) {
            dialog = Dialog(activity)
            dialog?.apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setCancelable(false)
                setContentView(R.layout.progress)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                show()
            }
        }
        count++
    }

    @Synchronized
    fun dismiss() {
        if (count > 0) {
            count--
            if (count == 0) {
                dialog?.let {
                    if (it.isShowing) {
                        it.dismiss()
                    }
                }
            }
        }
    }

    @Synchronized
    fun reset() {
        count = 0
        dialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    @Synchronized
    fun isShowing() = count > 0
}
