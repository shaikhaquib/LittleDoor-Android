package com.devative.littledoor.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.model.LoginModel
import com.devative.littledoor.model.UserDetails
import com.devative.littledoor.util.Progress
import com.devative.littledoor.util.Utility
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by AQUIB RASHID SHAIKH on 13-03-2023.
 */
@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    var basicDetails: UserDetails.Data? = null
    val progress: Progress by lazy {
        Progress(this)
    }
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mainViewModel.fetchUserData()
        mainViewModel.basicDetails.observe(this) {
            if (!it.isNullOrEmpty()) {
                basicDetails = it[0]

            }
        }
        if (Utility.getPrfString(this, Constants.AUTH_TOKEN).isNotEmpty())
            mainViewModel.getUserDetails()
    }
}