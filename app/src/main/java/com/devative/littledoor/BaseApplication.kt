package com.devative.littledoor

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Created by AQUIB RASHID SHAIKH on 20-03-2023.
 */
@HiltAndroidApp
class BaseApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // Initialize App Check and install the debug provider.
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}