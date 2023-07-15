package com.devative.littledoor.util
import timber.log.Timber

/**
 * Created by AQUIB RASHID SHAIKH on 20-03-2023.
 */

object Logger {
    fun d(message: String, vararg args: Any?) {
        Timber.d(message, *args)
    }

    fun e(message: String, vararg args: Any?) {
        Timber.e(message, *args)
    }

    fun i(message: String, vararg args: Any?) {
        Timber.i(message, *args)
    }

    fun w(message: String, vararg args: Any?) {
        Timber.w(message, *args)
    }

    fun wtf(message: String, vararg args: Any?) {
        Timber.wtf(message, *args)
    }
}
