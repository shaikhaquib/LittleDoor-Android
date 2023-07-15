package com.devative.littledoor.architecturalComponents.repository

import com.devative.littledoor.architecturalComponents.apicall.APIClient
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiHelper: APIClient
) {
    suspend fun getOTPPatientLogin(mobileNo:String, isPatient:String) = apiHelper.getOTPPatientLogin(mobileNo,isPatient)
    suspend fun getVerifyOTPPatientLogin(mobileNo:String, otp:String) = apiHelper.getVerifyOTPPatientLogin(mobileNo,otp)
    suspend fun getAllCities() = apiHelper.getAllCities()
    suspend fun getQuestions() = apiHelper.getQuestions()
    suspend fun createPatient(name:String, gender:String, dob:String, email:String, city_id:String) = apiHelper.createPatient(name, gender, dob, email, city_id)

}