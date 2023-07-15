package com.devative.littledoor.architecturalComponents.apicall

import android.content.Context
import com.devative.littledoor.architecturalComponents.helper.Constants.AUTH_TOKEN
import com.devative.littledoor.util.Logger
import com.devative.littledoor.util.Utility
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor (val context: Context) : Interceptor {

    /*companion object{
        var token = ""
    }*/

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        if (Utility.getPrfString(context,AUTH_TOKEN).isNotEmpty()) {
            request.addHeader("Authorization", "Bearer ${Utility.getPrfString(context,AUTH_TOKEN)}")
        }else {
            Logger.d("AuthInterceptor", " no tocken intercept: ")
        }
        return chain.proceed(request.build())
    }
}