package com.devative.littledoor.util
import timber.log.Timber

/**
 * Created by AQUIB RASHID SHAIKH on 20-03-2023.
 */

object Logger {
    fun d(message: String?, args: String?) {
        Timber.d(message, args)
    }

    fun e(message: String?, args: String?) {
        Timber.e(message, args)
    }

    fun i(message: String?, args: String?) {
        Timber.i(message, args)
    }

    fun w(message: String?, args: String?) {
        Timber.w(message, args)
    }

    fun wtf(message: String?, args: String?) {
        Timber.wtf(message, args)
    }
}
