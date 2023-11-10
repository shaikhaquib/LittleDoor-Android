package com.devative.littledoor.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.devative.littledoor.R
import com.devative.littledoor.adapter.TimeSlotAdapterByDate
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.architecturalComponents.viewmodel.TransactionViewModel
import com.devative.littledoor.databinding.ActivityBookAppointmentBinding
import com.devative.littledoor.model.AvailableSlotModel
import com.devative.littledoor.model.CreateOrderModel
import com.devative.littledoor.model.DoctotorListRes
import com.devative.littledoor.util.PaymentPromptSheet
import com.devative.littledoor.util.Utility
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultListener
import com.razorpay.PaymentResultWithDataListener
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookAppointment : BaseActivity(), TimeSlotAdapterByDate.TimeSlotAdapterByDateEvent,
    PaymentResultWithDataListener {
    val binding: ActivityBookAppointmentBinding by lazy {
        ActivityBookAppointmentBinding.inflate(layoutInflater)
    }
    val thDetails: DoctotorListRes.Data by lazy {
        intent.getSerializableExtra(Constants.TH_DETAILS) as DoctotorListRes.Data
    }
    val slotList = ArrayList<AvailableSlotModel.Data>()
    var selectedSlot: AvailableSlotModel.Data? = null
    private val vm: MainViewModel by viewModels()
    private val trModel: TransactionViewModel by viewModels()
    private var formattedDate = ""
    private var paymentData:PaymentData? = null
    private var orderData: CreateOrderModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        vm.fetchUserData()
        vm.basicDetails.observe(this){
            if (!it.isNullOrEmpty()){
                basicDetails = it[0]
            }
        }
        setData()
        val currentDate = Calendar.getInstance().time
        formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate)
        val dayId = getDayID(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
        getAvaibaleSLot(dayId, formattedDate)
        setupCalendar()
        setUPObserver()

        binding.btnProceed.setOnClickListener {
            /*slot_id:2
            appointment_date:2023-08-22*/
            //
            if (selectedSlot != null) {
                PaymentPromptSheet(object : PaymentPromptSheet.PaymentPromptEvent {
                    override fun onDismiss() {
                        Utility.errorToast(applicationContext, "Payment canceled")
                    }

                    override fun onPayment() {
                        // startPayment()
                        val dataMap = HashMap<String, Any>()
                        dataMap["patient_id"] = basicDetails?.pateint_id!!
                        dataMap["doctor_id"] = thDetails.id
                        dataMap["amount"] = thDetails.doctor_session_charge
                        trModel.createRazorPayOrder(dataMap)
                    }
                }).show(supportFragmentManager, PaymentPromptSheet.TAG)
            }
        }
        initRazorPay()

    }

    private fun initRazorPay() {
        Checkout.preload(applicationContext)
        val co = Checkout()
        co.setKeyID("rzp_test_pQVzKuWl7tuXu1")
    }


    private fun startPayment(id:String) {
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name", getString(R.string.app_name))
            options.put("description", "Appointment with ${thDetails.name}")
            options.put("currency", "INR")
            options.put("order_id", id)
            options.put(
                "amount",
                " ${(thDetails.doctor_session_charge.toIntOrNull() ?: 0) * 100}"
            ) // Amount in paise (e.g., 10000 paise = ₹100)

            co.open(this, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUPObserver() {
        vm.availableSlots.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        if (!it.data.data.isNullOrEmpty()) {
                            slotList.clear()
                            slotList.addAll(it.data.data as ArrayList)
                            binding.rvTimeSlot.adapter = TimeSlotAdapterByDate(this, slotList, this)
                            binding.txtError.visibility = View.GONE
                            binding.rvTimeSlot.visibility = View.VISIBLE
                        } else {
                            binding.txtError.text = "No Slots Available for the day"
                            binding.txtError.visibility = View.VISIBLE
                            binding.rvTimeSlot.visibility = View.GONE
                        }
                    } else {
                        binding.txtError.text = "No Slots Available for the day"
                        binding.txtError.visibility = View.VISIBLE
                        binding.rvTimeSlot.visibility = View.GONE
                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
        trModel.createRazorPayOrder.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        orderData = it.data
                        it.data.data?.let {
                            startPayment(it.id)
                        }
                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
        trModel.verifyOrder.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["doctor_id"] = thDetails.id
                        hashMap["slot_id"] = selectedSlot!!.id
                        hashMap["appointment_date"] = formattedDate
                        vm.bookAppointment(hashMap)
                    }else{
                        Utility.errorToast(applicationContext, it.data?.message?:getString(R.string.some_thing_went_wrong))
                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
        vm.bookAppointment.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress?.show()
                }

                Status.SUCCESS -> {
                    progress?.dismiss()
                    if (it.data?.status == true) {
                        Toasty.success(applicationContext, it.data.message).show()
                        finish()
                    } else {
                        Toasty.error(applicationContext, it.data!!.message).show()
                    }
                }

                Status.ERROR -> {
                    progress?.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }

    }

    private fun setupCalendar() {
        val currentDate = Calendar.getInstance()
        binding.calendarView.minDate = currentDate.timeInMillis
        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            val selectedDayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK)
            formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            val dayId = getDayID(selectedDayOfWeek)
            getAvaibaleSLot(dayId, formattedDate)

        }
    }

    private fun getAvaibaleSLot(dayId: Int, formattedDate: String) {
        val hashMap = HashMap<String, Any>()
        hashMap["doctor_id"] = thDetails.id
        hashMap["day_id"] = dayId
        hashMap["date"] = formattedDate
        vm.getAvailableSLotByDate(hashMap)
    }

    private fun getDayID(selectedDayOfWeek: Int): Int {
        val dayId = when (selectedDayOfWeek) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1 // Invalid day
        }
        return dayId
    }

    private fun setData() {
        thDetails.apply {
            binding.apply {
                txtName.text = "$name - $category_name"
                txtCharges.text = "$doctor_session_charge"
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemSelected(data: AvailableSlotModel.Data) {
        selectedSlot = data
        binding.btnProceed.isClickable =  true
        binding.btnProceed.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.primary))
    }




    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
       /* Utility.successToast(applicationContext, "Success - $p1")
        Log.d("TAG", "onPaymentSuccess: $p0 ### $p1")*/
        paymentData = p1
        if (selectedSlot == null) {
            Utility.errorToast(applicationContext, "Please select the available slot")
        } else {
            val hashMap = HashMap<String, Any>()
            hashMap["payment_id"] = orderData?.paymentId?:0
            hashMap["order_id"] = orderData?.data?.id?:"0"
            hashMap["razorpay_payment_id"] = paymentData?.paymentId?:"0"
            hashMap["razorpay_order_id"] = paymentData?.orderId?:"0"
            hashMap["razorpay_signature"] = paymentData?.signature?:"0"
            trModel.verifyOrder(hashMap)
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
       Utility.successToast(applicationContext, "Payment failed")
    }


}