package com.devative.littledoor.architecturalComponents.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devative.littledoor.architecturalComponents.helper.Resource
import com.devative.littledoor.architecturalComponents.repository.MainRepository
import com.devative.littledoor.architecturalComponents.room.UserDao
import com.devative.littledoor.model.CreateOrderModel
import com.devative.littledoor.model.DoctorTransactionRes
import com.devative.littledoor.model.GeneralResponse
import com.devative.littledoor.model.RevenueDetails
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
    val getDoctorTransaction = MutableLiveData<Resource<DoctorTransactionRes>>()
    val getRevenue = MutableLiveData<Resource<RevenueDetails>>()
    val withdrawalRequest = MutableLiveData<Resource<GeneralResponse>>()
    val verifyOrder = MutableLiveData<Resource<GeneralResponse>>()
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
    fun withdrawalRequest(sendData: HashMap<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        withdrawalRequest.postValue(Resource.loading(null))
        try {
            mainRepository.withdrawalRequest(sendData).let {
                if (it.isSuccessful) {
                    withdrawalRequest.postValue(Resource.success(it.body()))
                } else {
                    withdrawalRequest.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            withdrawalRequest.postValue(Resource.error(e.message.toString(), null))
        }
    }
    fun verifyOrder(sendData: HashMap<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        verifyOrder.postValue(Resource.loading(null))
        try {
            mainRepository.verifyOrder(sendData).let {
                if (it.isSuccessful) {
                    verifyOrder.postValue(Resource.success(it.body()))
                } else {
                    verifyOrder.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            verifyOrder.postValue(Resource.error(e.message.toString(), null))
        }
    }
    fun getRevenue() = CoroutineScope(Dispatchers.IO).launch {
        getRevenue.postValue(Resource.loading(null))
        try {
            mainRepository.getRevenue().let {
                if (it.isSuccessful) {
                    getRevenue.postValue(Resource.success(it.body()))
                } else {
                    getRevenue.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            getRevenue.postValue(Resource.error(e.message.toString(), null))
        }
    }
    fun getDoctorTransaction() = CoroutineScope(Dispatchers.IO).launch {
        getDoctorTransaction.postValue(Resource.loading(null))
        try {
            mainRepository.getDoctorTransaction().let {
                if (it.isSuccessful) {
                    getDoctorTransaction.postValue(Resource.success(it.body()))
                } else {
                    getRevenue.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            getDoctorTransaction.postValue(Resource.error(e.message.toString(), null))
        }
    }

}