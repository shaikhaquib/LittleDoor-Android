package com.devative.littledoor.architecturalComponents.viewmodel

import androidx.lifecycle.*
import com.devative.littledoor.architecturalComponents.helper.Resource
import com.devative.littledoor.architecturalComponents.room.UserDao
import com.devative.littledoor.architecturalComponents.repository.MainRepository
import com.devative.littledoor.model.AvailableSlotModel
import com.devative.littledoor.model.DoctotorListRes
import com.devative.littledoor.model.GeneralResponse
import com.devative.littledoor.model.GetAllCitiesResponse
import com.devative.littledoor.model.GetAllQuestions
import com.devative.littledoor.model.LoginModel
import com.devative.littledoor.model.UserDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel  @Inject constructor(
    private val userDao: UserDao,
    private val mainRepository: MainRepository
): ViewModel(){

    companion object{
        fun getViewModel(owner: ViewModelStoreOwner):MainViewModel{
            return ViewModelProvider(owner)[MainViewModel::class.java]
        }
    }


    var basicDetails: LiveData<List<UserDetails.Data>> = MutableLiveData()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun fetchUserData(){
        viewModelScope.launch {
            basicDetails = userDao.getAll()
        }
    }
    fun insertUserData(data:UserDetails.Data){
        CoroutineScope(Dispatchers.IO).launch {
           userDao.insertAll(data)
        }
    }
    fun deleteUserData(){
        CoroutineScope(Dispatchers.IO).launch {
           userDao.deleteAll()
        }
    }



    private val _userDetails = MutableLiveData<Resource<UserDetails>>()
    val userDetails : LiveData<Resource<UserDetails>>
        get() = _userDetails

    private val _OTPSend = MutableLiveData<Resource<GeneralResponse>>()
    val OTPSend : LiveData<Resource<GeneralResponse>>
        get() = _OTPSend

    private val _verifyOtp = MutableLiveData<Resource<LoginModel>>()
    val verifyOtp : LiveData<Resource<LoginModel>>
        get() = _verifyOtp

    private val _getCities = MutableLiveData<Resource<GetAllCitiesResponse>>()
    val getCities : LiveData<Resource<GetAllCitiesResponse>>
        get() = _getCities

    private val _getQuestions = MutableLiveData<Resource<GetAllQuestions>>()
    val getQuestions : LiveData<Resource<GetAllQuestions>>
        get() = _getQuestions

    private val _createPatient = MutableLiveData<Resource<GeneralResponse>>()
    val createPatient : LiveData<Resource<GeneralResponse>>
        get() = _createPatient

    private val _saveMCQ = MutableLiveData<Resource<GeneralResponse>>()
    val saveMCQ : LiveData<Resource<GeneralResponse>>
        get() = _saveMCQ

    val doctorList = MutableLiveData<Resource<DoctotorListRes>>()
    val bookAppointment = MutableLiveData<Resource<GeneralResponse>>()
    val availableSlots = MutableLiveData<Resource<AvailableSlotModel>>()

    fun getOTPPatientLogin(mobileNo:String, isPatient:String) = CoroutineScope(Dispatchers.IO).launch {
        _OTPSend.postValue(Resource.loading(null))
        try {
            mainRepository.getOTPPatientLogin(mobileNo,isPatient).let {
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
    fun getVerifyOTPPatientLogin(mobileNo:String, otp:String) = CoroutineScope(Dispatchers.IO).launch {
        _verifyOtp.postValue(Resource.loading(null))
        try {
            mainRepository.getVerifyOTPPatientLogin(mobileNo,otp).let {
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
                    it.body()?.data?.let {data->
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

    fun createUser(name:String, gender:String, dob:String, email:String, city_id:String) = CoroutineScope(Dispatchers.IO).launch {
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




}