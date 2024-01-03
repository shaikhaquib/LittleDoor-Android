package com.devative.littledoor.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.devative.littledoor.ChatUi.liveStreaming.LiveStreaming
import com.devative.littledoor.R
import com.devative.littledoor.activity.DailyGeneralActivity
import com.devative.littledoor.activity.MainActivity
import com.devative.littledoor.activity.UpdateProfile
import com.devative.littledoor.activity.UserNotificationActivity
import com.devative.littledoor.adapter.EmoteAdapter
import com.devative.littledoor.adapter.SliderAdapter
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DailyJournalVM
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.HomeFragmentBinding
import com.devative.littledoor.model.EmotModel
import com.devative.littledoor.model.SliderModel
import com.devative.littledoor.model.UserAppointmentModel.*
import com.devative.littledoor.model.UserDetails
import com.devative.littledoor.util.DailyGeneraleBottomSheet
import com.devative.littledoor.util.Progress
import com.devative.littledoor.util.QuoteManager
import com.devative.littledoor.util.Utility
import com.devative.littledoor.util.Utility.isCurrentDate
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Created by AQUIB RASHID SHAIKH on 20-03-2023.
 */
@AndroidEntryPoint
class HomeFragment  : Fragment() {
    private var basicDetails: UserDetails.Data? = null
    private lateinit var binding: HomeFragmentBinding
    private lateinit var vm: MainViewModel
    private val dailyJournalVM: DailyJournalVM by viewModels()
    private val emoteList = ArrayList<EmotModel.Data>()
    private lateinit var sliderAdapter: SliderAdapter
    private val sliderItems = ArrayList<SliderModel.Data>()
    private lateinit var autoScrollJob: Job
    private val progress:Progress by lazy {
        Progress(requireActivity() as AppCompatActivity)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dayGreetings()
        vm = MainViewModel.getViewModel(requireActivity())
        vm.fetchUserData()
        vm.basicDetails.observe(viewLifecycleOwner){
            if (!it.isNullOrEmpty()){
                basicDetails = it[0]
                updateUI()
            }
        }
        binding.imgProfile.setOnClickListener {
            startActivity(Intent(requireContext(),UpdateProfile::class.java))
        }
        binding.imageView4.setOnClickListener {
            startActivity(Intent(requireContext(), UserNotificationActivity::class.java))
        }
        binding.layoutJournal.setOnClickListener {
            startActivity(Intent(requireContext(),DailyGeneralActivity::class.java))
        }
        binding.rvEmote.adapter = EmoteAdapter(requireActivity(),emoteList,object :
            EmoteAdapter.EmoteAdapterEvent {
            override fun onclick(position: Int) {
                val dialog = DailyGeneraleBottomSheet(emoteList,position,object :
                    DailyGeneraleBottomSheet.DailyGeneraleBottomSheetEvent {
                    override fun onSubmit(id: Int, message: String) {
                        val dataMap = HashMap<String, Any>()
                        dataMap["emotion_id"] = id
                        dataMap["date"] = Utility.getCurrentDateFormatted("yyyy-MM-dd")
                        dataMap["message"] = message
                        dailyJournalVM.postJournal(dataMap)
                    }
                })
                dialog.show(requireActivity().supportFragmentManager, "ImagePickerDialog")
            }
        }, selectable = false)
        QuoteManager(requireActivity(),binding.txtQuote,binding.txtQuoteAuthor)
        observe()
        binding.btnBookNow.setOnClickListener {
            (requireActivity() as MainActivity).setNavigationSelection(R.id.bottom_navigation_search)
        }
    }

    private fun observe() {
        dailyJournalVM.getEmote()
        vm.getUserBookedAppointment()
        vm.getPromotions()
        dailyJournalVM.getEmote.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
               //   progress.show()
                }

                Status.SUCCESS -> {
                 //   progress.dismiss()
                    if (it.data?.status == true) {
                        emoteList.clear()
                        if (it.data.data.isNotEmpty()) {
                            emoteList.addAll(it.data.data)
                            binding.rvEmote.adapter?.notifyDataSetChanged()
                        }

                    }
                }

                Status.ERROR -> {
                 //   progress.dismiss()
                }

            }
        }
        dailyJournalVM.postJournal.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true){
                        Utility.successToast(requireContext(),it.data.message)
                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            requireContext(),
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
        vm.getUserBookedAppointment.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    if (!progress.isShowing())
                        progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        if (it.data.data.isNotEmpty()) {
                            todayAppointment(it.data.data as ArrayList)
                        }else{
                            binding.liBookSession.visibility = View.VISIBLE
                            binding.liJoinSession.visibility = View.GONE
                        }
                    } else {

                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                     /* Utility.errorToast(
                            requireContext(),
                            it1
                        )*/
                    }
                }

            }
        }

        vm.promotions.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        sliderItems.clear()
                        if (it.data.data.isNotEmpty()) {
                            sliderItems.addAll(it.data.data as ArrayList)
                            handlePromotionAdapter()
                        }
                    } else {
                        Utility.errorToast(
                            requireContext(),
                            getString(R.string.some_thing_went_wrong)
                        )
                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Utility.errorToast(
                            requireContext(),
                            getString(R.string.some_thing_went_wrong)
                        )
                    }
                }

            }
        }


    }

    private fun updateUI() {
        binding.txtUserName.text = basicDetails?.name
        basicDetails?.image_url?.let { it1 -> binding.imgProfile.load(it1,R.drawable.profile_view)}
        binding.imgProfile.borderColor =
            ContextCompat.getColor(requireContext(), R.color.grey_primary)
        binding.imgProfile.borderWidth = 2
    }

    private fun dayGreetings() {
        val date = Date()
        val cal = Calendar.getInstance()
        cal.time = date
        val hour = cal[Calendar.HOUR_OF_DAY]

        //Set greeting

        //Set greeting
        when (hour) {
            in 12..16 -> {
                binding.txtGreet.text = "${getString(R.string.afternoon_greet)},"
            }
            in 17..20 -> {
                binding.txtGreet.text = "${getString(R.string.evening_greet)},"
            }
            in 21..23 -> {
                binding.txtGreet.text = "${getString(R.string.night_greet)},"
            }
            in 0..4 -> {
                binding.txtGreet.text = "${getString(R.string.night_greet)},"
            }
            else -> {
                binding.txtGreet.text = "${getString(R.string.morning_greet)},"
            }
        }
    }
    fun todayAppointment(dataList: ArrayList<Data>) {
        val currentTime = Calendar.getInstance()
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val todayData = dataList.filter {
            val appointmentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.apointmnet_date)
            val appointmentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(it.slot_time)

            val appointmentDateTime = Calendar.getInstance()
            appointmentDateTime.time = appointmentTime!!
            appointmentDateTime.set(Calendar.YEAR, today.get(Calendar.YEAR))
            appointmentDateTime.set(Calendar.MONTH, today.get(Calendar.MONTH))
            appointmentDateTime.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH))

            // Set the appointment time
            appointmentDateTime.set(Calendar.HOUR_OF_DAY, appointmentDateTime.get(Calendar.HOUR_OF_DAY))
            appointmentDateTime.set(Calendar.MINUTE, appointmentDateTime.get(Calendar.MINUTE))
            appointmentDateTime.set(Calendar.SECOND, 0)
            appointmentDateTime.set(Calendar.MILLISECOND, 0)

            // Add 30 minutes to the appointment time
            appointmentDateTime.add(Calendar.MINUTE, 30)

            appointmentDate == today.time && appointmentDateTime.after(currentTime)
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        val upcomingAppointment = todayData.minByOrNull {
            val appointmentDateTime = sdf.parse("${it.apointmnet_date} ${it.slot_time}")
            appointmentDateTime
        }
        if (upcomingAppointment != null) {
            binding.liBookSession.visibility = View.GONE
            binding.liJoinSession.visibility = View.VISIBLE
            binding.txtTHName.text = "${upcomingAppointment.doctor_name}"
            binding.txtSlotTime.text = "Today at ${upcomingAppointment.slot_time}"

            binding.btnJoinNow.setOnClickListener {
                //(requireActivity() as MainActivity).setNavigationSelection(R.id.bottom_navigation_search)
                startActivity(Intent(context, LiveStreaming::class.java)
                    .putExtra("CHANNEL_ID",upcomingAppointment.id.toString())
                    .putExtra("IS_HOST",true))

            }

        } else {
            binding.liBookSession.visibility = View.VISIBLE
            binding.liJoinSession.visibility = View.GONE
        }


    }

    private fun handlePromotionAdapter() {
        sliderAdapter = SliderAdapter(sliderItems)
        binding.viewPager.adapter = sliderAdapter
       // binding.viewPager.setPageTransformer(FadePageTransformer())


        autoScrollJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(3000)
                withContext(Dispatchers.Main) {
                    binding.viewPager.currentItem = (binding.viewPager.currentItem + 1) % sliderItems.size
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (this::autoScrollJob.isInitialized)
            autoScrollJob.cancel()
    }


}