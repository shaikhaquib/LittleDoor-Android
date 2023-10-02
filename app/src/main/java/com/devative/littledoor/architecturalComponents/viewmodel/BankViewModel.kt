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
import com.devative.littledoor.model.BankDetailsModel
import com.devative.littledoor.model.CategoryResponse
import com.devative.littledoor.model.DailyJournalModel
import com.devative.littledoor.model.DoctorDetailsResponse
import com.devative.littledoor.model.EmotModel
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
class BankViewModel @Inject constructor(
    private val userDao: UserDao,
    private val mainRepository: MainRepository
) : ViewModel() {
    
    val bankDetails = MutableLiveData<Resource<BankDetailsModel>>()
    val deleteBankDetails = MutableLiveData<Resource<GeneralResponse>>()
    val addBankDetails = MutableLiveData<Resource<GeneralResponse>>()

    fun getBankDetails() = CoroutineScope(Dispatchers.IO).launch {
        bankDetails.postValue(Resource.loading(null))
        try {
            mainRepository.getBankDetails().let {
                if (it.isSuccessful) {
                    bankDetails.postValue(Resource.success(it.body()))
                } else {
                    bankDetails.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            bankDetails.postValue(Resource.error(e.message.toString(), null))
        }
    }
    fun deleteBankDetails(id:Int) = CoroutineScope(Dispatchers.IO).launch {
        deleteBankDetails.postValue(Resource.loading(null))
        try {
            mainRepository.deleteBankDetails(id).let {
                if (it.isSuccessful) {
                    deleteBankDetails.postValue(Resource.success(it.body()))
                } else {
                    deleteBankDetails.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            deleteBankDetails.postValue(Resource.error(e.message.toString(), null))
        }
    }
    fun addBankDetails(data: HashMap<String,Any>) = CoroutineScope(Dispatchers.IO).launch {
        addBankDetails.postValue(Resource.loading(null))
        try {
            mainRepository.addBankDetails(data).let {
                if (it.isSuccessful) {
                    addBankDetails.postValue(Resource.success(it.body()))
                } else {
                    addBankDetails.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            addBankDetails.postValue(Resource.error(e.message.toString(), null))
        }
    }

}