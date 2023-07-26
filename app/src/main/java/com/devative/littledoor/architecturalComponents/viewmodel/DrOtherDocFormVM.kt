package com.devative.littledoor.architecturalComponents.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devative.littledoor.R
import com.devative.littledoor.activity.drForms.ActivityAddAppreciation
import com.devative.littledoor.activity.drForms.ActivityAddEducationForm
import com.devative.littledoor.activity.drForms.ActivityAddExperience
import com.devative.littledoor.activity.drForms.ActivityUploadOtherDocument
import com.devative.littledoor.architecturalComponents.apicall.APIClient
import com.devative.littledoor.architecturalComponents.helper.FileUploader
import com.devative.littledoor.architecturalComponents.helper.Resource
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.repository.MainRepository
import com.devative.littledoor.architecturalComponents.room.UserDao
import com.devative.littledoor.model.CategoryResponse
import com.devative.littledoor.model.DoctorDetailsResponse
import com.devative.littledoor.model.GeneralResponse
import com.devative.littledoor.model.LoginModel
import com.devative.littledoor.model.SubCategoryResponse
import com.devative.littledoor.util.Logger
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by AQUIB RASHID SHAIKH on 15-07-2023.
 */
@HiltViewModel
class DrOtherDocFormVM @Inject constructor(
    private val userDao: UserDao,
    private val mainRepository: MainRepository
) : ViewModel() {
    val formData = MutableLiveData<ArrayList<ActivityUploadOtherDocument.OtherDocumentForm>>()

    init {
        formData.value = ArrayList()
    }

    fun addFormItem(item: ActivityUploadOtherDocument.OtherDocumentForm) {
        val currentList = formData.value ?: ArrayList()
        currentList.add(item)
        formData.value = currentList
    }

    fun deleteFormItem(index: Int) {
        val currentList = formData.value ?: ArrayList()
        if (index in 0 until currentList.size) {
            currentList.removeAt(index)
            formData.value = currentList
        }
    }


}