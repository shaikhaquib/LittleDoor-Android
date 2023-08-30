package com.devative.littledoor.architecturalComponents

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.devative.littledoor.architecturalComponents.apicall.APIClient
import com.devative.littledoor.architecturalComponents.apicall.AuthInterceptor
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.room.UserDao
import com.devative.littledoor.architecturalComponents.room.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class  HiltAppIOModule {

    @Provides
    fun provideBaseUrl() = Constants.BASE_URL

    @Singleton
    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context) = run {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val authInterceptor = AuthInterceptor(context)
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(ChuckerInterceptor(context))
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, BASE_URL:String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()
    @Provides
    @Singleton
    fun provideAPIClient(retrofit: Retrofit) = retrofit.create(APIClient::class.java)!!

    @Provides
    fun provideStudentDao(@ApplicationContext appContext: Context) : UserDao {
        return UserDatabase.getDatabase(appContext).userDao()
    }



}