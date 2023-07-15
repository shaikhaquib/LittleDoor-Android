package com.devative.littledoor.architecturalComponents.apicall

import com.devative.littledoor.model.GeneralResponse
import com.devative.littledoor.model.GetAllCitiesResponse
import com.devative.littledoor.model.GetAllQuestions
import com.devative.littledoor.model.LoginModel
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface APIClient {

    @FormUrlEncoded
    @POST("admin/register/login")
    suspend fun getOTPPatientLogin(
        @Field("mobile_no") mobile_no: String,
        @Field("is_patient") is_patient: String,
    ): Response<GeneralResponse>

    @FormUrlEncoded
    @POST("admin/register/login")
    suspend fun getVerifyOTPPatientLogin(
        @Field("mobile_no") mobile_no: String,
        @Field("otp") otp: String,
    ): Response<LoginModel>

    @GET("admin/get/cities")
    suspend fun getAllCities(): Response<GetAllCitiesResponse>

    @GET("admin/get-all/questions/options")
    suspend fun getQuestions(): Response<GetAllQuestions>

    @FormUrlEncoded
    @POST("patient/create/patient")
    suspend fun createPatient(
        @Field("name") name: String,
        @Field("gender") gender: String,
        @Field("dob") dob: String,
        @Field("email") email: String,
        @Field("city_id") city_id: String
    ): Response<GeneralResponse>

}