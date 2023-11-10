package com.devative.littledoor.architecturalComponents.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.devative.littledoor.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.devative.littledoor.model.UserAppointmentModel
import java.io.File
import java.io.FileOutputStream
import java.util.UUID


object Constants {
    const val  WELCOME_BANNER_SHOWN = "welcomeBannerShown"
    const val  SESSION_EXPIRED = "session_expired"
    const val IS_DOCTOR: String = "Doctor"
    const val IS_DR_Reg_Finish: String = "IS_DR_Reg_Finish"
    const val BASE_URL = "https://www.littledoor.in/api/"
    const val PHONE_NO = "phone"
    const val AUTH_TOKEN = "token"
    const val FORM_EDIT_POSITION = "position"
    const val FORM_EDIT_DATA = "FORM_EDIT_DATA"
    const val TH_DETAILS = "TH_DETAILS"
    const val TH_REGISTERED = "Registered"
    const val FCM_TOKEN = "FCM_TOKEN"
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

        if (currentDate >= targetDate) {
            return "$dateString is already passed"
        }

        val months = targetDate.get(Calendar.MONTH) - currentDate.get(Calendar.MONTH)
        val weeks = targetDate.get(Calendar.WEEK_OF_YEAR) - currentDate.get(Calendar.WEEK_OF_YEAR)
        val days = targetDate.get(Calendar.DAY_OF_YEAR) - currentDate.get(Calendar.DAY_OF_YEAR)
        val hours = targetDate.get(Calendar.HOUR_OF_DAY) - currentDate.get(Calendar.HOUR_OF_DAY)
        val minutes = targetDate.get(Calendar.MINUTE) - currentDate.get(Calendar.MINUTE)

        return when {
            months > 0 -> "Will start in $months month"
            weeks > 0 -> "Will start in $weeks week"
            days > 0 -> "Will start in $days day"
            hours > 0 -> "Will start in $hours hour"
            minutes > 0 -> "Will start in $minutes minute"
            else -> "$dateString is already passed"
        }
    }

    fun hasDatePassed(targetDate: String, targetTime: String): Boolean {
        val targetDateTime = "$targetDate $targetTime"
        val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        val currentDate = Date()
        val targetDateTimeObj = dateTimeFormatter.parse(targetDateTime)
        return currentDate.after(targetDateTimeObj)
    }
    fun filterAndSortData(dataList: List<UserAppointmentModel.Data>, filterCode: Int): List<UserAppointmentModel.Data> {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return when (filterCode) {
            1 -> dataList.filter { it.apointmnet_date == currentDate }
            2 -> dataList.filter { dateFormatter.parse(it.apointmnet_date) > dateFormatter.parse(currentDate) }
            3 -> dataList.filter { dateFormatter.parse(it.apointmnet_date) < dateFormatter.parse(currentDate) }
            else -> throw IllegalArgumentException("Invalid filter code")
        }.sortedBy { it.apointmnet_date }
    }

    fun convertTimestampToDate(timestamp: Long, format:String = "yyyy-MM-dd HH:mm:ss"): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }


    fun uriToFile(context: Context, uri: Uri, extension:String = "jpg"): File? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val file = File(context.cacheDir, "${UUID.randomUUID()}_file.$extension")
                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var read: Int

                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }

                inputStream.close()
                outputStream.flush()
                outputStream.close()

                return file
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun shareText(context: Context, message: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }


}
