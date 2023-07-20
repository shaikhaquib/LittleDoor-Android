package com.devative.littledoor.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.model.LoginModel
import com.devative.littledoor.model.UserDetails
import com.devative.littledoor.util.Progress
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by AQUIB RASHID SHAIKH on 13-03-2023.
 */
@AndroidEntryPoint
open class BaseActivity: AppCompatActivity(){
    var basicDetails: UserDetails.Data? = null
    var progress: Progress? = null
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        mainViewModel = MainViewModel.getViewModel(this)
        progress = Progress(this)
        mainViewModel.fetchUserData()
        mainViewModel.basicDetails.observe(this){
            if (!it.isNullOrEmpty()){
                basicDetails = it[0]
            }
        }
    }
}