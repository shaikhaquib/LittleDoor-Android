package com.devative.littledoor.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.SensorPrivacyManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.RemoteException
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.model.DrRegistrationMasterModel
import com.devative.littledoor.util.Utility
import com.devative.littledoor.util.Utility.CHANNEL_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    lateinit var vm: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        vm = MainViewModel.getViewModel(this)
        vm.fetchUserData()
        vm.basicDetails.observe(this) {
            if (!it.isNullOrEmpty()) {
                basicDetails = it[0]
            }
        }
        initNotificationChannel()
        Handler(Looper.getMainLooper()).postDelayed({
            if (basicDetails == null) {
                if (Utility.getPrefBoolean(applicationContext, Constants.WELCOME_BANNER_SHOWN))
                    startActivity(Intent(applicationContext, SignUpActivity::class.java))
                else
                    startActivity(Intent(applicationContext, WelcomeBanner::class.java))
            } else {
                if (basicDetails?.doctor_id == null)
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                else{
                    if (basicDetails?.status == 1 || Utility.getPrefBoolean(applicationContext,Constants.IS_DR_Reg_Finish))
                        startActivity(Intent(applicationContext, MainActivity::class.java).putExtra(Constants.IS_DOCTOR,true))
                    else
                        startActivity(Intent(applicationContext, DoctorRegistrationMaster::class.java))
                }
            }

            finish()
        }, 800)
    }


    private fun initNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channelName = "${getString(R.string.app_name)}_General"
        val channelDescription = "General Message"
        val importance = NotificationManagerCompat.IMPORTANCE_HIGH
        val channel = NotificationChannelCompat.Builder(CHANNEL_ID, importance).apply {
            setName(channelName)
            setDescription(channelDescription)
        }
        NotificationManagerCompat.from(applicationContext)
            .createNotificationChannel(channel.build())
    }

}