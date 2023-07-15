package com.devative.littledoor.util

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.devative.littledoor.R
import com.devative.littledoor.activity.MainActivity
import es.dmoral.toasty.Toasty
import java.util.*

/**
 * Created by AQUIB RASHID SHAIKH on 11-03-2023.
 */
object Utility {
    const val CHANNEL_ID = "102"
    fun showNotification(applicationContext: Context, message: String?, title: String?) {
        val random = Random()
        val notificationId = random.nextInt(9999 - 1000) + 1000
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("title",title)
        intent.putExtra("message",message)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for Android Oreo and above

        val inboxStyle = NotificationCompat.BigTextStyle()
        inboxStyle.bigText(message)

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)

        val resultPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        builder.setContentIntent(resultPendingIntent)
        notificationManager.notify(notificationId, builder.build())
    }

    public fun savePrefString(context: Context,key : String,value : String){
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("USER",Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putString(key,value)
        editor.apply()
        editor.commit()
    }

    fun getPrfString(context: Context,key : String) : String{
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("USER",Context.MODE_PRIVATE)
        val value  = sharedPreferences.getString(key,"")
        return value?:""
    }
    fun savePrefBoolean(context: Context,key : String,value : Boolean){
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("USER",Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putBoolean(key,value)
        editor.apply()
        editor.commit()
    }

    fun getPrefBoolean(context: Context,key : String) : Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("USER",Context.MODE_PRIVATE)
        val value : Boolean = sharedPreferences.getBoolean(key,false);
        return value
    }
    fun clearPreference(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("USER",Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun errorToast(applicationContext: Context,message: String){
        Toasty.error(applicationContext,message).show()
    }
    fun successToast(applicationContext: Context,message: String){
        Toasty.success(applicationContext,message).show()
    }
    fun infoToast(applicationContext: Context,message: String){
        Toasty.info(applicationContext,message).show()
    }

}