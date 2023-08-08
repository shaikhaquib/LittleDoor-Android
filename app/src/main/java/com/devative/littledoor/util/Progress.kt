package com.devative.littledoor.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.devative.littledoor.R

class Progress( var activity: AppCompatActivity) {
    var dialog: Dialog? = null

    //..we need the context else we can not create the dialog so get context in construction
    fun show() {
        if (activity.isDestroyed){
            return
        }
        dialog = Dialog(activity)
        dialog?.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(R.layout.progress)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }

    //..also create a method which will hide the dialog when some work is done
    fun dismiss() {
        dialog?.let {
            if (it.isShowing){
                it.dismiss()
            }
        }
    }

    fun isShowing() = dialog != null && dialog?.isShowing == true

}