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
import com.devative.littledoor.model.SessionDetails
import com.devative.littledoor.model.SkillResponse
import com.devative.littledoor.model.SubCategoryResponse
import com.devative.littledoor.model.TimeSLotModel
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
class UpdateSessionDetailsVM @Inject constructor(
    private val userDao: UserDao,
    private val mainRepository: MainRepository
) : ViewModel() {
    
    val setSessionAmount = MutableLiveData<Resource<GeneralResponse>>()
    val getSessionAmount = MutableLiveData<Resource<SessionDetails>>()
    val setAvailability = MutableLiveData<Resource<GeneralResponse>>()
    val getAllTimeSlot = MutableLiveData<Resource<TimeSLotModel>>()


    fun setSessionAmount(drID:Int, amount: String) = CoroutineScope(Dispatchers.IO).launch {
        setSessionAmount.postValue(Resource.loading(null))
        try {
            mainRepository.setSessionCharge(drID,amount).let {
                if (it.isSuccessful) {
                    setSessionAmount.postValue(Resource.success(it.body()))
                } else {
                    setSessionAmount.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            setSessionAmount.postValue(Resource.error(e.message.toString(), null))
        }
    }
    fun setAvailability(drID:Int, status: Int) = CoroutineScope(Dispatchers.IO).launch {
        setAvailability.postValue(Resource.loading(null))
        try {
            mainRepository.setAvailability(drID,status).let {
                if (it.isSuccessful) {
                    setAvailability.postValue(Resource.success(it.body()))
                } else {
                    setAvailability.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            setAvailability.postValue(Resource.error(e.message.toString(), null))
        }
    }

    fun getSessionDetails(drID:Int) = CoroutineScope(Dispatchers.IO).launch {
        getSessionAmount.postValue(Resource.loading(null))
        try {
            mainRepository.getSessionCharge(drID).let {
                if (it.isSuccessful) {
                    getSessionAmount.postValue(Resource.success(it.body()))
                } else {
                    getSessionAmount.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            getSessionAmount.postValue(Resource.error(e.message.toString(), null))
        }
    }
    fun getAllTimeSlots() = CoroutineScope(Dispatchers.IO).launch {
        getAllTimeSlot.postValue(Resource.loading(null))
        try {
            mainRepository.getAllTimeSLots().let {
                if (it.isSuccessful) {
                    getAllTimeSlot.postValue(Resource.success(it.body()))
                } else {
                    getAllTimeSlot.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            getAllTimeSlot.postValue(Resource.error(e.message.toString(), null))
        }
    }

}