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
class DailyJournalVM @Inject constructor(
    private val userDao: UserDao,
    private val mainRepository: MainRepository
) : ViewModel() {
    
    val postJournal = MutableLiveData<Resource<GeneralResponse>>()
    val deleteJournal = MutableLiveData<Resource<GeneralResponse>>()
    val getEmote = MutableLiveData<Resource<EmotModel>>()
    val getJournal = MutableLiveData<Resource<DailyJournalModel>>()


    fun postJournal(sendData: HashMap<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        postJournal.postValue(Resource.loading(null))
        try {
            mainRepository.postJournal(sendData).let {
                if (it.isSuccessful) {
                    postJournal.postValue(Resource.success(it.body()))
                } else {
                    postJournal.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            postJournal.postValue(Resource.error(e.message.toString(), null))
        }
    }
    fun deleteJournal(id:Int) = CoroutineScope(Dispatchers.IO).launch {
        deleteJournal.postValue(Resource.loading(null))
        try {
            mainRepository.deleteJournal(id).let {
                if (it.isSuccessful) {
                    deleteJournal.postValue(Resource.success(it.body()))
                } else {
                    deleteJournal.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            deleteJournal.postValue(Resource.error(e.message.toString(), null))
        }
    }

    fun getEmote() = CoroutineScope(Dispatchers.IO).launch {
        getEmote.postValue(Resource.loading(null))
        try {
            mainRepository.getAllEmotions().let {
                if (it.isSuccessful) {
                    getEmote.postValue(Resource.success(it.body()))
                } else {
                    getEmote.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            getEmote.postValue(Resource.error(e.message.toString(), null))
        }
    }
    fun getJournal() = CoroutineScope(Dispatchers.IO).launch {
        getJournal.postValue(Resource.loading(null))
        try {
            mainRepository.getJournal().let {
                if (it.isSuccessful) {
                    getJournal.postValue(Resource.success(it.body()))
                } else {
                    getJournal.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            getJournal.postValue(Resource.error(e.message.toString(), null))
        }
    }

}