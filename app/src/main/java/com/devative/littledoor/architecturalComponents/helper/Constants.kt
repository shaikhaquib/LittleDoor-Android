package com.devative.littledoor.architecturalComponents.helper

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.devative.littledoor.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.bumptech.glide.load.engine.DiskCacheStrategy



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
        Glide.with(context.getApplicationContext())
            .load(url)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .override(width, height)
            .centerCrop()
            .into(this)
    }

    val monthNames = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    fun convertDateFormat(dateString: String, targetFormat: String, dateCurrentFormat:String = "yyyy-MM-dd"): String {
        val sourceFormat = SimpleDateFormat(dateCurrentFormat)
        val sourceDate = sourceFormat.parse(dateString)
        val targetFormatObj = SimpleDateFormat(targetFormat)
        return targetFormatObj.format(sourceDate)
    }

    @SuppressLint("NewApi")
    fun getTimeRemaining(dateString: String): String {

        val formatter = SimpleDateFormat("yyyy-MM-dd, hh:mm a", Locale.getDefault())
        val currentDate = Calendar.getInstance()
        val targetDate = Calendar.getInstance()
        targetDate.time = formatter.parse(dateString)

        val months = targetDate.get(Calendar.MONTH) - currentDate.get(Calendar.MONTH)
        val weeks = targetDate.get(Calendar.WEEK_OF_YEAR) - currentDate.get(Calendar.WEEK_OF_YEAR)
        val days = targetDate.get(Calendar.DAY_OF_YEAR) - currentDate.get(Calendar.DAY_OF_YEAR)
        val hours = targetDate.get(Calendar.HOUR_OF_DAY) - currentDate.get(Calendar.HOUR_OF_DAY)
        val minutes = targetDate.get(Calendar.MINUTE) - currentDate.get(Calendar.MINUTE)

        return when {
            months > 0 -> "Start in $months month"
            weeks > 0 -> "Start in $weeks week"
            days > 0 -> "Start in $days day"
            hours > 0 -> "Start in $hours hour"
            minutes > 0 -> "Start in $minutes minute"
            else -> "Time has already passed"
        }
    }

    fun hasDatePassed(targetDate: String, targetTime: String): Boolean {
        val targetDateTime = "$targetDate $targetTime"
        val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        val currentDate = Date()
        val targetDateTimeObj = dateTimeFormatter.parse(targetDateTime)
        return currentDate.after(targetDateTimeObj)
    }


}
