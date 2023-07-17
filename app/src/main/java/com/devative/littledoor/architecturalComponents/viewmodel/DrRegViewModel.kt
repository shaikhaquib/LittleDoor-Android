package com.devative.littledoor.architecturalComponents.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devative.littledoor.activity.drForms.ActivityAddExperience
import com.devative.littledoor.architecturalComponents.helper.Resource
import com.devative.littledoor.architecturalComponents.repository.MainRepository
import com.devative.littledoor.architecturalComponents.room.UserDao
import com.devative.littledoor.model.CategoryResponse
import com.devative.littledoor.model.DoctorDetailsResponse
import com.devative.littledoor.model.GeneralResponse
import com.devative.littledoor.model.LoginModel
import com.devative.littledoor.model.SubCategoryResponse
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


    val subCategoryData= MutableLiveData<Resource<SubCategoryResponse>>()
    val categoryData = MutableLiveData<Resource<CategoryResponse>>()
    val doctorDetailsData =  MutableLiveData<Resource<DoctorDetailsResponse>>()
    private val _formData = MutableLiveData<ArrayList<ActivityAddExperience.FormData>>()
    val formData: LiveData<ArrayList<ActivityAddExperience.FormData>> get() = _formData

    init {
        _formData.value = ArrayList()
    }

    fun addFormItem(item: ActivityAddExperience.FormData) {
        val currentList = _formData.value ?: ArrayList()
        currentList.add(item)
        _formData.value = currentList
    }

    fun deleteFormItem(index: Int) {
        val currentList = _formData.value ?: ArrayList()
        if (index in 0 until currentList.size) {
            currentList.removeAt(index)
            _formData.value = currentList
        }
    }


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
    fun getSubCategory(catID:String) = CoroutineScope(Dispatchers.IO).launch {
        subCategoryData.postValue(Resource.loading(null))
        try {
            mainRepository.getSubCategory(catID).let {
                if (it.isSuccessful) {
                    subCategoryData.postValue(Resource.success(it.body()))
                } else {
                    subCategoryData.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            subCategoryData.postValue(Resource.error(e.message.toString(), null))
        }
    }
    fun getCategory() = CoroutineScope(Dispatchers.IO).launch {
        categoryData.postValue(Resource.loading(null))
        try {
            mainRepository.getCategory().let {
                if (it.isSuccessful) {
                    categoryData.postValue(Resource.success(it.body()))
                } else {
                    categoryData.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            categoryData.postValue(Resource.error(e.message.toString(), null))
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


}