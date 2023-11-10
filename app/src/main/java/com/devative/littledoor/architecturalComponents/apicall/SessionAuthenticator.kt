package com.devative.littledoor.architecturalComponents.apicall

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Handler
import android.os.Looper
import com.devative.littledoor.activity.SignUpActivity
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.util.Utility
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

/**
 * Created by AQUIB RASHID SHAIKH on 06-11-2023.
 */
class SessionAuthenticator @Inject constructor(
    private val context: Context
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) {
            Handler(Looper.getMainLooper()).post {
                Utility.errorToast(context, "Session expired! Please login again to continue")
                if (Utility.getPrfString(context, Constants.AUTH_TOKEN).isNotEmpty()) {
                    Utility.clearPreference(context)
                    context.startActivity(
                        Intent(context, SignUpActivity::class.java)
                            .putExtra(Constants.SESSION_EXPIRED, true)
                            .setFlags(FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            }
            return null
        }

        // If the response is not 401, return null to indicate that no further authentication attempts should be made
        return null
    }
}