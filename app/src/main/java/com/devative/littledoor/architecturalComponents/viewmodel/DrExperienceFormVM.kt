package com.devative.littledoor.architecturalComponents.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devative.littledoor.activity.drForms.ActivityAddEducationForm
import com.devative.littledoor.activity.drForms.ActivityAddExperience
import com.devative.littledoor.architecturalComponents.helper.Resource
import com.devative.littledoor.architecturalComponents.repository.MainRepository
import com.devative.littledoor.architecturalComponents.room.UserDao
import com.devative.littledoor.model.CategoryResponse
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
class DrExperienceFormVM @Inject constructor(
    private val userDao: UserDao,
    private val mainRepository: MainRepository
) : ViewModel() {

    val subCategoryData= MutableLiveData<Resource<SubCategoryResponse>>()
    val categoryData = MutableLiveData<Resource<CategoryResponse>>()

    val formData = MutableLiveData<ArrayList<ActivityAddExperience.FormData>>()



    fun addFormItem(item: ActivityAddExperience.FormData) {
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


}