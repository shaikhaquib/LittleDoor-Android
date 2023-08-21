package com.devative.littledoor.architecturalComponents.helper

import android.widget.ImageView
import androidx.constraintlayout.helper.widget.MotionPlaceholder
import com.bumptech.glide.Glide
import com.devative.littledoor.R


object Constants {
    const val  WELCOME_BANNER_SHOWN = "welcomeBannerShown"
    const val IS_DOCTOR: String = "Doctor"
    const val IS_DR_Reg_Finish: String = "IS_DR_Reg_Finish"
    const val BASE_URL = "https://www.littledoor.in/api/"
    const val PHONE_NO = "phone"
    const val AUTH_TOKEN = "token"
    const val FORM_EDIT_POSITION = "position"
    const val FORM_EDIT_DATA = "FORM_EDIT_DATA"
    const val TH_DETAILS = "TH_DETAILS"
    var isDoctor = false

    fun ImageView.load(url: String,placeholder: Int = R.color.primary) {
        Glide.with(context)
            .load(url)
            .placeholder(placeholder)
            .into(this)
    }

    val monthNames = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
}
