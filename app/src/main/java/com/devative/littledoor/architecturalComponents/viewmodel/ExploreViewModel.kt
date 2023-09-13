package com.devative.littledoor.architecturalComponents.viewmodel

import androidx.lifecycle.*
import com.devative.littledoor.activity.drForms.ActivityUploadOtherDocument
import com.devative.littledoor.architecturalComponents.helper.Resource
import com.devative.littledoor.architecturalComponents.room.UserDao
import com.devative.littledoor.architecturalComponents.repository.MainRepository
import com.devative.littledoor.model.AvailableSlotModel
import com.devative.littledoor.model.DoctotorListRes
import com.devative.littledoor.model.GeneralResponse
import com.devative.littledoor.model.GetAllCitiesResponse
import com.devative.littledoor.model.GetAllQuestions
import com.devative.littledoor.model.LoginModel
import com.devative.littledoor.model.PostCommentModel
import com.devative.littledoor.model.PostModel
import com.devative.littledoor.model.UserAppointmentModel
import com.devative.littledoor.model.UserDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val userDao: UserDao,
    private val mainRepository: MainRepository
) : ViewModel() {

    val getAllPost = MutableLiveData<Resource<PostModel>>()
    val getAllPostUser = MutableLiveData<Resource<PostModel>>()
    val getAllPostUserLikes = MutableLiveData<Resource<PostModel>>()
    val getAllPostUserComments = MutableLiveData<Resource<PostModel>>()
    val likePost = MutableLiveData<Resource<GeneralResponse>>()
    val addComment = MutableLiveData<Resource<GeneralResponse>>()
    val deletePost = MutableLiveData<Resource<GeneralResponse>>()
    val comments = MutableLiveData<Resource<PostCommentModel>>()

    fun likePost(sendData: HashMap<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        likePost.postValue(Resource.loading(null))
        try {
            mainRepository.likePost(sendData).let {
                if (it.isSuccessful) {
                    likePost.postValue(Resource.success(it.body()))
                } else {
                    likePost.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            likePost.postValue(Resource.error(e.message.toString(), null))
        }
    }
    fun getAllPost() = CoroutineScope(Dispatchers.IO).launch {
        getAllPost.postValue(Resource.loading(null))
        try {
            mainRepository.getAllPost().let {
                if (it.isSuccessful) {
                    getAllPost.postValue(Resource.success(it.body()))
                } else {
                    getAllPost.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            getAllPost.postValue(Resource.error(e.message.toString(), null))
        }
    }
    fun getAllPostUser() = CoroutineScope(Dispatchers.IO).launch {
        getAllPostUser.postValue(Resource.loading(null))
        try {
            mainRepository.getAllPostUser().let {
                if (it.isSuccessful) {
                    getAllPostUser.postValue(Resource.success(it.body()))
                } else {
                    getAllPostUser.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            getAllPostUser.postValue(Resource.error(e.message.toString(), null))
        }
    }
    fun getAllPostUserLikes() = CoroutineScope(Dispatchers.IO).launch {
        getAllPostUserLikes.postValue(Resource.loading(null))
        try {
            mainRepository.getAllPostUserLikes().let {
                if (it.isSuccessful) {
                    getAllPostUserLikes.postValue(Resource.success(it.body()))
                } else {
                    getAllPostUserLikes.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            getAllPostUserLikes.postValue(Resource.error(e.message.toString(), null))
        }
    }
    fun getAllPostUserComment() = CoroutineScope(Dispatchers.IO).launch {
        getAllPostUserComments.postValue(Resource.loading(null))
        try {
            mainRepository.getAllPostUserComment().let {
                if (it.isSuccessful) {
                    getAllPostUserComments.postValue(Resource.success(it.body()))
                } else {
                    getAllPostUserComments.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            getAllPostUserComments.postValue(Resource.error(e.message.toString(), null))
        }
    }

    fun addComment(sendData: HashMap<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        addComment.postValue(Resource.loading(null))
        try {
            mainRepository.addComment(sendData).let {
                if (it.isSuccessful) {
                    addComment.postValue(Resource.success(it.body()))
                } else {
                    addComment.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            addComment.postValue(Resource.error(e.message.toString(), null))
        }
    }
    fun getComments(sendData: HashMap<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        comments.postValue(Resource.loading(null))
        try {
            mainRepository.getComments(sendData).let {
                if (it.isSuccessful) {
                    comments.postValue(Resource.success(it.body()))
                } else {
                    comments.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            comments.postValue(Resource.error(e.message.toString(), null))
        }
    }
    fun deletePost(sendData: HashMap<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        deletePost.postValue(Resource.loading(null))
        try {
            mainRepository.addComment(sendData).let {
                if (it.isSuccessful) {
                    deletePost.postValue(Resource.success(it.body()))
                } else {
                    deletePost.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            deletePost.postValue(Resource.error(e.message.toString(), null))
        }
    }


}