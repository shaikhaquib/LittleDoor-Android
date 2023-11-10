package com.devative.littledoor.architecturalComponents.viewmodel

import android.content.Intent
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.*
import com.devative.littledoor.ChatUi.ChatActivity
import com.devative.littledoor.architecturalComponents.helper.Resource
import com.devative.littledoor.architecturalComponents.room.UserDao
import com.devative.littledoor.architecturalComponents.repository.MainRepository
import com.devative.littledoor.model.AvailableSlotModel
import com.devative.littledoor.model.ChatListResponse
import com.devative.littledoor.model.CreateChatModel
import com.devative.littledoor.model.DRStatsModel
import com.devative.littledoor.model.DoctotorListRes
import com.devative.littledoor.model.GeneralResponse
import com.devative.littledoor.model.GetAllCitiesResponse
import com.devative.littledoor.model.GetAllQuestions
import com.devative.littledoor.model.LoginModel
import com.devative.littledoor.model.NotificationResponse
import com.devative.littledoor.model.SliderModel
import com.devative.littledoor.model.UserAppointmentModel
import com.devative.littledoor.model.UserDetails
import com.devative.littledoor.util.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userDao: UserDao,
    private val mainRepository: MainRepository
) : ViewModel() {

    companion object {
        fun getViewModel(owner: ViewModelStoreOwner): MainViewModel {
            return ViewModelProvider(owner)[MainViewModel::class.java]
        }
    }


    var basicDetails: LiveData<List<UserDetails.Data>> = MutableLiveData()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun fetchUserData() {
        viewModelScope.launch {
            basicDetails = userDao.getAll()
        }
    }

    fun insertUserData(data: UserDetails.Data) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.insertAll(data)
        }
    }

    fun deleteUserData() {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.deleteAll()
            logout()
        }
    }


    private val _userDetails = MutableLiveData<Resource<UserDetails>>()
    val userDetails: LiveData<Resource<UserDetails>>
        get() = _userDetails

    private val _OTPSend = MutableLiveData<Resource<GeneralResponse>>()
    val OTPSend: LiveData<Resource<GeneralResponse>>
        get() = _OTPSend

    private val _verifyOtp = MutableLiveData<Resource<LoginModel>>()
    val verifyOtp: LiveData<Resource<LoginModel>>
        get() = _verifyOtp

    private val _getCities = MutableLiveData<Resource<GetAllCitiesResponse>>()
    val getCities: LiveData<Resource<GetAllCitiesResponse>>
        get() = _getCities

    private val _getQuestions = MutableLiveData<Resource<GetAllQuestions>>()
    val getQuestions: LiveData<Resource<GetAllQuestions>>
        get() = _getQuestions

    private val _createPatient = MutableLiveData<Resource<GeneralResponse>>()
    val createPatient: LiveData<Resource<GeneralResponse>>
        get() = _createPatient

    private val _saveMCQ = MutableLiveData<Resource<GeneralResponse>>()
    val saveMCQ: LiveData<Resource<GeneralResponse>>
        get() = _saveMCQ

    val doctorList = MutableLiveData<Resource<DoctotorListRes>>()
    val bookAppointment = MutableLiveData<Resource<GeneralResponse>>()
    val availableSlots = MutableLiveData<Resource<AvailableSlotModel>>()
    val getUserBookedAppointment = MutableLiveData<Resource<UserAppointmentModel>>()
    val promotions = MutableLiveData<Resource<SliderModel>>()
    val createChat = MutableLiveData<Resource<CreateChatModel>>()
    val getChat = MutableLiveData<Resource<ChatListResponse>>()
    val getNotifications = MutableLiveData<Resource<NotificationResponse>>()
    val notificationReadReceipt = MutableLiveData<Resource<GeneralResponse>>()
    val getDoctorStats = MutableLiveData<Resource<DRStatsModel>>()

    fun getOTPPatientLogin(mobileNo: String, isPatient: String) =
        CoroutineScope(Dispatchers.IO).launch {
            _OTPSend.postValue(Resource.loading(null))
            try {
                mainRepository.getOTPPatientLogin(mobileNo, isPatient).let {
                    if (it.isSuccessful) {
                        _OTPSend.postValue(Resource.success(it.body()))
                    } else {
                        _OTPSend.postValue(
                            Resource.error(
                                "Server Error",
                                null
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                _OTPSend.postValue(Resource.error(e.message.toString(), null))
            }
        }

    fun getVerifyOTPPatientLogin(mobileNo: String, otp: String) =
        CoroutineScope(Dispatchers.IO).launch {
            _verifyOtp.postValue(Resource.loading(null))
            try {
                mainRepository.getVerifyOTPPatientLogin(mobileNo, otp).let {
                    if (it.isSuccessful) {
                        _verifyOtp.postValue(Resource.success(it.body()))
                    } else {
                        _verifyOtp.postValue(
                            Resource.error(
                                "Server Error",
                                null
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                _verifyOtp.postValue(Resource.error(e.message.toString(), null))
            }
        }

    fun getUserDetails() = CoroutineScope(Dispatchers.IO).launch {
        _userDetails.postValue(Resource.loading(null))
        try {
            mainRepository.getUserDetails().let {
                if (it.isSuccessful) {
                    _userDetails.postValue(Resource.success(it.body()))
                    it.body()?.data?.let { data ->
                        if (data.doctor_id != null || data.pateint_id != null)
                        insertUserData(data)
                    }
                } else {
                    _userDetails.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            _userDetails.postValue(Resource.error(e.message.toString(), null))
        }
    }

    fun getPromotions() = CoroutineScope(Dispatchers.IO).launch {
        promotions.postValue(Resource.loading(null))
        try {
            mainRepository.getPromotions().let {
                if (it.isSuccessful) {
                    promotions.postValue(Resource.success(it.body()))
                } else {
                    promotions.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            promotions.postValue(Resource.error(e.message.toString(), null))
        }
    }

    fun getCities() = CoroutineScope(Dispatchers.IO).launch {
        _getCities.postValue(Resource.loading(null))
        try {
            mainRepository.getAllCities().let {
                if (it.isSuccessful) {
                    _getCities.postValue(Resource.success(it.body()))
                } else {
                    _getCities.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            _getCities.postValue(Resource.error(e.message.toString(), null))
        }
    }

    fun getQuestions() = CoroutineScope(Dispatchers.IO).launch {
        _getQuestions.postValue(Resource.loading(null))
        try {
            mainRepository.getQuestions().let {
                if (it.isSuccessful) {
                    _getQuestions.postValue(Resource.success(it.body()))
                } else {
                    _getQuestions.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            _getQuestions.postValue(Resource.error(e.message.toString(), null))
        }
    }

    fun createUser(name: String, gender: String, dob: String, email: String, city_id: String) =
        CoroutineScope(Dispatchers.IO).launch {
            _createPatient.postValue(Resource.loading(null))
            try {
                mainRepository.createPatient(name, gender, dob, email, city_id).let {
                    if (it.isSuccessful) {
                        _createPatient.postValue(Resource.success(it.body()))
                    } else {
                        _createPatient.postValue(
                            Resource.error(
                                "Server Error",
                                null
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                _createPatient.postValue(Resource.error(e.message.toString(), null))
            }
        }

    fun saveMCQResult(result: HashMap<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        _saveMCQ.postValue(Resource.loading(null))
        try {
            mainRepository.saveMCQResult(result).let {
                if (it.isSuccessful) {
                    _saveMCQ.postValue(Resource.success(it.body()))
                } else {
                    _saveMCQ.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            _saveMCQ.postValue(Resource.error(e.message.toString(), null))
        }
    }

    fun getDoctorList() = CoroutineScope(Dispatchers.IO).launch {
        doctorList.postValue(Resource.loading(null))
        try {
            mainRepository.getDoctorList().let {
                if (it.isSuccessful) {
                    doctorList.postValue(Resource.success(it.body()))
                } else {
                    doctorList.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            doctorList.postValue(Resource.error(e.message.toString(), null))
        }
    }

    fun bookAppointment(data: HashMap<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        bookAppointment.postValue(Resource.loading(null))
        try {
            mainRepository.bookAppointment(data).let {
                if (it.isSuccessful) {
                    bookAppointment.postValue(Resource.success(it.body()))
                } else {
                    bookAppointment.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            bookAppointment.postValue(Resource.error(e.message.toString(), null))
        }
    }

    fun getAvailableSLotByDate(data: HashMap<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        availableSlots.postValue(Resource.loading(null))
        try {
            mainRepository.getAvailableSLotByDate(data).let {
                if (it.isSuccessful) {
                    availableSlots.postValue(Resource.success(it.body()))
                } else {
                    availableSlots.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            availableSlots.postValue(Resource.error(e.message.toString(), null))
        }
    }


    fun getUserBookedAppointment() = CoroutineScope(Dispatchers.IO).launch {
        getUserBookedAppointment.postValue(Resource.loading(null))
        try {
            mainRepository.getUserBookedAppointment().let {
                if (it.isSuccessful) {
                    getUserBookedAppointment.postValue(Resource.success(it.body()))
                } else {
                    getUserBookedAppointment.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            getUserBookedAppointment.postValue(Resource.error(e.message.toString(), null))
        }
    }

    fun getChatList() = CoroutineScope(Dispatchers.IO).launch {
        getChat.postValue(Resource.loading(null))
        try {
            mainRepository.getChat().let {
                if (it.isSuccessful) {
                    getChat.postValue(Resource.success(it.body()))
                } else {
                    getChat.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            getChat.postValue(Resource.error(e.message.toString(), null))
        }
    }

    fun createChat(sendData: HashMap<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        createChat.postValue(Resource.loading(null))
        try {
            mainRepository.createChat(sendData).let {
                if (it.isSuccessful) {
                    createChat.postValue(Resource.success(it.body()))
                } else {
                    createChat.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            createChat.postValue(Resource.error(e.message.toString(), null))
        }
    }

    fun getNotifications() = CoroutineScope(Dispatchers.IO).launch {
        getNotifications.postValue(Resource.loading(null))
        try {
            mainRepository.getNotification().let {
                if (it.isSuccessful) {
                    getNotifications.postValue(Resource.success(it.body()))
                } else {
                    getNotifications.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            getNotifications.postValue(Resource.error(e.message.toString(), null))
        }
    }

    fun notificationReadReceipt(sendData: HashMap<String, Any>) =
        CoroutineScope(Dispatchers.IO).launch {
            notificationReadReceipt.postValue(Resource.loading(null))
            try {
                mainRepository.notificationReadReceipt(sendData).let {
                    if (it.isSuccessful) {
                        notificationReadReceipt.postValue(Resource.success(it.body()))
                    } else {
                        notificationReadReceipt.postValue(
                            Resource.error(
                                "Server Error",
                                null
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                notificationReadReceipt.postValue(Resource.error(e.message.toString(), null))
            }
        }

    fun logout() = CoroutineScope(Dispatchers.IO).launch {
        try {
            mainRepository.logout().let {
                Log.d("TAG", "logout: ${it.body()?.message}")
            }
        } catch (e: Exception) {
            Log.d("TAG", "logout: ${e.message.toString()}")
        }
    }


    fun getDoctorStats() = CoroutineScope(Dispatchers.IO).launch {
        getDoctorStats.postValue(Resource.loading(null))
        try {
            mainRepository.getDoctorStats().let {
                if (it.isSuccessful) {
                    getDoctorStats.postValue(Resource.success(it.body()))
                } else {
                    getDoctorStats.postValue(
                        Resource.error(
                            "Server Error",
                            null
                        )
                    )
                }
            }
        } catch (e: Exception) {
            getDoctorStats.postValue(Resource.error(e.message.toString(), null))
        }
    }

}