package com.devative.littledoor.architecturalComponents.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devative.littledoor.R
import com.devative.littledoor.activity.drForms.ActivityAddExperience
import com.devative.littledoor.architecturalComponents.apicall.APIClient
import com.devative.littledoor.architecturalComponents.helper.FileUploader
import com.devative.littledoor.architecturalComponents.helper.Resource
import com.devative.littledoor.architecturalComponents.repository.MainRepository
import com.devative.littledoor.architecturalComponents.room.UserDao
import com.devative.littledoor.model.CategoryResponse
import com.devative.littledoor.model.DoctorDetailsResponse
import com.devative.littledoor.model.GeneralResponse
import com.devative.littledoor.model.LoginModel
import com.devative.littledoor.model.SkillResponse
import com.devative.littledoor.model.SubCategoryResponse
import com.devative.littledoor.util.Logger
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by AQUIB RASHID SHAIKH on 15-07-2023.
 */
@HiltViewModel
class DrRegViewModel @Inject constructor(
    private val userDao: UserDao,
    private val mainRepository: MainRepository
) : ViewModel() {
    
    private val _registerTherapist = MutableLiveData<Resource<GeneralResponse>>()
    val registerTherapist: LiveData<Resource<GeneralResponse>>
        get() = _registerTherapist

    private val _verifyOtpTher = MutableLiveData<Resource<LoginModel>>()
    val verifyOtpTher : LiveData<Resource<LoginModel>>
        get() = _verifyOtpTher


    val uploadResponse = MutableLiveData<Resource<GeneralResponse>>()
    val doctorDetailsData =  MutableLiveData<Resource<DoctorDetailsResponse>>()
    val skillData =  MutableLiveData<Resource<SkillResponse>>()

    fun registerTherapist(sendData: HashMap<String, String>) = CoroutineScope(Dispatchers.IO).launch {
        _registerTherapist.postValue(Resource.loading(null))
        try {
            mainRepository.createTherapist(sendData).let {
                if (it.isSuccessful) {
                    _registerTherapist.postValue(Resource.success(it.body()))
                } else {
                    _registerTherapist.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            _registerTherapist.postValue(Resource.error(e.message.toString(), null))
        }
    }

    fun getVerifyOTPTherapistLogin(mobileNo:String, otp:String) = CoroutineScope(Dispatchers.IO).launch {
        _verifyOtpTher.postValue(Resource.loading(null))
        try {
            mainRepository.getVerifyOTPPatientLogin(mobileNo,otp).let {
                if (it.isSuccessful) {
                    _verifyOtpTher.postValue(Resource.success(it.body()))
                } else {
                    _verifyOtpTher.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            _verifyOtpTher.postValue(Resource.error(e.message.toString(), null))
        }
    }

    fun getDoctorDetails() = CoroutineScope(Dispatchers.IO).launch {
        doctorDetailsData.postValue(Resource.loading(null))
        try {
            mainRepository.getDoctorDetails().let {
                if (it.isSuccessful) {
                    doctorDetailsData.postValue(Resource.success(it.body()))
                } else {
                    doctorDetailsData.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            doctorDetailsData.postValue(Resource.error(e.message.toString(), null))
        }
    }
    fun getSkill() = CoroutineScope(Dispatchers.IO).launch {
        skillData.postValue(Resource.loading(null))
        try {
            mainRepository.getSkill().let {
                if (it.isSuccessful) {
                    skillData.postValue(Resource.success(it.body()))
                } else {
                    skillData.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            skillData.postValue(Resource.error(e.message.toString(), null))
        }
    }


    fun uploadData(
        context: Context,
        fileMap: Map<String, Uri>,
        dataMap: Map<String, String>
    ) = CoroutineScope(Dispatchers.IO).launch {
        uploadResponse.postValue(Resource.loading(null))
        FileUploader.uploadFiles(context,
            APIClient.THERAPIST_ADD_DETAILS,
            fileMap,
            dataMap,
            object : FileUploader.UploadCallback {
                override fun onFileUploadError(errorMessage: String?) {
                    uploadResponse.postValue(Resource.error(errorMessage!!, null))
                }

                override fun onAllFilesUploaded(string: String) {
                    Logger.d("File upload", string)
                    if (string != null) {
                        val gson = Gson()
                        val data: GeneralResponse =
                            gson.fromJson(string, GeneralResponse::class.java)
                        if (data.status)
                            uploadResponse.postValue(Resource.success(data))
                        else
                            uploadResponse.postValue(Resource.error(data.message, null))
                    }else{
                        uploadResponse.postValue(Resource.error(context.getString(R.string.some_thing_went_wrong), null))
                    }
                }
            })
    }


}