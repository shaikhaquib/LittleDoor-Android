package com.devative.littledoor.architecturalComponents.apicall

import androidx.room.Delete
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.model.AvailableSlotModel
import com.devative.littledoor.model.CategoryResponse
import com.devative.littledoor.model.DailyJournalModel
import com.devative.littledoor.model.DoctorDetailsResponse
import com.devative.littledoor.model.DoctotorListRes
import com.devative.littledoor.model.EmotModel
import com.devative.littledoor.model.GeneralResponse
import com.devative.littledoor.model.GetAllCitiesResponse
import com.devative.littledoor.model.GetAllQuestions
import com.devative.littledoor.model.LoginModel
import com.devative.littledoor.model.SessionDetails
import com.devative.littledoor.model.SkillResponse
import com.devative.littledoor.model.SubCategoryResponse
import com.devative.littledoor.model.TimeSLotModel
import com.devative.littledoor.model.UserDetails
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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

    @GET("admin/get/user/details")
    suspend fun getUserDetails(): Response<UserDetails>

    @GET("admin/get/cities")
    suspend fun getAllCities(): Response<GetAllCitiesResponse>

    @GET("admin/get/questions")
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

    @POST("admin/save/patient/question/response")
    suspend fun saveMCQResult(@Body sendData: HashMap<String, Any>): Response<GeneralResponse>

    @POST("doctor/create/doctor")
    suspend fun createTherapist(@Body sendData: HashMap<String, String>): Response<GeneralResponse>


    @GET("admin/get/categroy")
    suspend fun getCategory(): Response<CategoryResponse>

    @FormUrlEncoded
    @POST("admin/get/sub/category")
    suspend fun getSubCategory(
        @Field("category_id") name: String
    ): Response<SubCategoryResponse>

    @GET("doctor/get/doctor/time/slot")
    suspend fun getSessionCharge(): Response<SessionDetails>

    @FormUrlEncoded
    @POST("doctor/create/update/sesion/charge")
    suspend fun setSessionCharge(
        @Field("doctor_id") id: Int,
        @Field("session_charge_amount") amount: String
    ): Response<GeneralResponse>

    @FormUrlEncoded
    @POST("doctor/update/availability/consultancy")
    suspend fun setDrAvailability(
        @Field("doctor_id") id: Int,
        @Field("consultancy_status") status: Int
    ): Response<GeneralResponse>

    @GET("doctor/get/details")
    suspend fun getDoctorDetails(): Response<DoctorDetailsResponse>

    @GET("admin/get/skills")
    suspend fun getSkill(): Response<SkillResponse>

    @GET("admin/get/doctor/list")
    suspend fun getDoctorList(): Response<DoctotorListRes>

    @GET("admin/get/all-slot")
    suspend fun getAllTimeSLots(): Response<TimeSLotModel>

    @GET("admin/get/all/emotions")
    suspend fun getAllEmotions(): Response<EmotModel>

    @POST("patient/add/daily/journal")
    suspend fun postJournal(@Body sendData: HashMap<String, Any>): Response<GeneralResponse>

    @GET("patient/get/daily/journal")
    suspend fun getJournal(): Response<DailyJournalModel>

    @POST("patient/get/available/slot/book")
    suspend fun getAvailableSLotByDate(@Body sendData: HashMap<String, Any>): Response<AvailableSlotModel>

    @POST("patient/book/appointment")
    suspend fun bookAppointment(@Body sendData: HashMap<String, Any>): Response<GeneralResponse>
    @DELETE("patient/delete/daily/journal/{id}")
    suspend fun deleteJournal(
        @Path(
            value = "id",
            encoded = true
        ) id: Int,
    ): Response<GeneralResponse>

    companion object {
        const val THERAPIST_ADD_DETAILS = "${Constants.BASE_URL}doctor/submit/details"
        const val UPDATE_PROFILE = "${Constants.BASE_URL}admin/update/user/details"
        const val DR_CREATE_TIMESLOT = "${Constants.BASE_URL}doctor/create/slot"
    }

}