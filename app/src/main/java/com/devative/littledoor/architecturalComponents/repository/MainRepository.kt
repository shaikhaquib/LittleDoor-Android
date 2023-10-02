package com.devative.littledoor.architecturalComponents.repository

import com.devative.littledoor.architecturalComponents.apicall.APIClient
import com.devative.littledoor.architecturalComponents.helper.Status
import retrofit2.http.Body
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiHelper: APIClient
) {
    suspend fun getOTPPatientLogin(mobileNo: String, isPatient: String) =
        apiHelper.getOTPPatientLogin(mobileNo, isPatient)

    suspend fun getVerifyOTPPatientLogin(mobileNo: String, otp: String) =
        apiHelper.getVerifyOTPPatientLogin(mobileNo, otp)

    suspend fun getUserDetails() = apiHelper.getUserDetails()
    suspend fun getAllCities() = apiHelper.getAllCities()
    suspend fun getQuestions() = apiHelper.getQuestions()
    suspend fun createPatient(
        name: String,
        gender: String,
        dob: String,
        email: String,
        city_id: String
    ) = apiHelper.createPatient(name, gender, dob, email, city_id)

    suspend fun saveMCQResult(data: HashMap<String, Any>) = apiHelper.saveMCQResult(data)
    suspend fun createTherapist(sendData: HashMap<String, String>) =
        apiHelper.createTherapist(sendData)

    suspend fun getSubCategory(id: String) = apiHelper.getSubCategory(id)
    suspend fun getSessionCharge(id: Int) = apiHelper.getSessionCharge()
    suspend fun setSessionCharge(id: Int, amount: String) = apiHelper.setSessionCharge(id, amount)
    suspend fun setAvailability(id: Int, status: Int) = apiHelper.setDrAvailability(id, status)
    suspend fun getCategory() = apiHelper.getCategory()
    suspend fun getDoctorDetails() = apiHelper.getDoctorDetails()
    suspend fun getSkill() = apiHelper.getSkill()
    suspend fun getDoctorList() = apiHelper.getDoctorList()
    suspend fun getAllTimeSLots() = apiHelper.getAllTimeSLots()
    suspend fun getAllEmotions() = apiHelper.getAllEmotions()
    suspend fun getJournal() = apiHelper.getJournal()
    suspend fun getUserBookedAppointment() = apiHelper.getUserBookedAppointment()
    suspend fun postJournal(sendData: HashMap<String, Any>) = apiHelper.postJournal(sendData)
    suspend fun getAvailableSLotByDate(sendData: HashMap<String, Any>) = apiHelper.getAvailableSLotByDate(sendData)
    suspend fun bookAppointment(sendData: HashMap<String, Any>) = apiHelper.bookAppointment(sendData)
    suspend fun deleteJournal(id:Int) = apiHelper.deleteJournal(id)
    suspend fun deletePost(id:Int) = apiHelper.deletePost(id)
    suspend fun getAllPost() = apiHelper.getAllPost()
    suspend fun getAllPostUser() = apiHelper.getAllPostUser()
    suspend fun getAllPostUserLikes() = apiHelper.getAllPostUserLikes()
    suspend fun getAllPostUserComment() = apiHelper.getAllPostUserComment()
    suspend fun addComment(sendData: HashMap<String, Any>) = apiHelper.addComment(sendData)
    suspend fun getComments(sendData: HashMap<String, Any>) = apiHelper.getComments(sendData)
    suspend fun likePost(sendData: HashMap<String, Any>) = apiHelper.likePost(sendData)
    suspend fun getBankDetails() = apiHelper.getBankDetails()
    suspend fun deleteBankDetails(id:Int) = apiHelper.deleteBankDetails(id)
    suspend fun addBankDetails(sendData: HashMap<String, Any>) = apiHelper.addBankDetails(sendData)

}