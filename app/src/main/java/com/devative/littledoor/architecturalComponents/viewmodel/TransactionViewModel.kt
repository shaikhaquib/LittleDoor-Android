package com.devative.littledoor.architecturalComponents.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devative.littledoor.architecturalComponents.helper.Resource
import com.devative.littledoor.architecturalComponents.repository.MainRepository
import com.devative.littledoor.architecturalComponents.room.UserDao
import com.devative.littledoor.model.CreateOrderModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by AQUIB RASHID SHAIKH on 03-11-2023.
 */
@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val userDao: UserDao,
    private val mainRepository: MainRepository
) : ViewModel() {

    val createRazorPayOrder = MutableLiveData<Resource<CreateOrderModel>>()
    fun createRazorPayOrder(sendData: HashMap<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        createRazorPayOrder.postValue(Resource.loading(null))
        try {
            mainRepository.createOrder(sendData).let {
                if (it.isSuccessful) {
                    createRazorPayOrder.postValue(Resource.success(it.body()))
                } else {
                    createRazorPayOrder.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            createRazorPayOrder.postValue(Resource.error(e.message.toString(), null))
        }
    }

}