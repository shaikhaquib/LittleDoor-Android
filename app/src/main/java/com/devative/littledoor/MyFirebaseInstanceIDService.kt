package com.devative.littledoor

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.devative.littledoor.activity.MainActivity
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.util.Utility
import com.devative.littledoor.util.Utility.CHANNEL_ID
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class MyFirebaseInstanceIDService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        var body: String? = null
        var title: String? = null
        val params: Map<String, String> = remoteMessage.data
        Log.d("TAG", "onMessageReceived: $params")
        var jObject: JSONObject? = null
        try {
            jObject = JSONObject(params)
            body = jObject.getString("body")
            title = jObject.getString("title")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        showNotification(body, title)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("TAG", "onNewToken: $token")
        Utility.savePrefString(applicationContext, Constants.FCM_TOKEN, token)
    }

    fun showNotification(message: String?, title: String?) {
        val random = Random()
        val notificationId = random.nextInt(9999 - 1000) + 1000
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("title",title)
        intent.putExtra("message",message)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for Android Oreo and above

        val inboxStyle = NotificationCompat.BigTextStyle()
        inboxStyle.bigText(message)

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
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

}