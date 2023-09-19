package com.devative.littledoor.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.devative.littledoor.R
import com.devative.littledoor.activity.DailyGeneralActivity
import com.devative.littledoor.activity.MainActivity
import com.devative.littledoor.activity.UpdateProfile
import com.devative.littledoor.adapter.EmoteAdapter
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DailyJournalVM
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.HomeFragmentBinding
import com.devative.littledoor.model.EmotModel
import com.devative.littledoor.model.UserAppointmentModel.*
import com.devative.littledoor.model.UserDetails
import com.devative.littledoor.util.DailyGeneraleBottomSheet
import com.devative.littledoor.util.Progress
import com.devative.littledoor.util.QuoteManager
import com.devative.littledoor.util.Utility
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
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
    private val progress:Progress by lazy {
        Progress(requireActivity() as AppCompatActivity)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                    progress?.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true){
                        Utility.successToast(requireContext(),it.data.message)
                    }
                }

                Status.ERROR -> {
                    progress?.dismiss()
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
                      Utility.errorToast(
                            requireContext(),
                            it1
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
        binding.imgProfile.borderWidth = 10
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
    fun todayAppointment(dataList: java.util.ArrayList<Data>) {

        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())


        val todayData = dataList.filter {
            val appointmentTime = it.slot_time.toLowerCase(Locale.getDefault())
            val appointmentDateTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(appointmentTime)
            val calendar = Calendar.getInstance()
            calendar.time = appointmentDateTime!!
            calendar.add(Calendar.MINUTE, 30) // Add 30 minutes
            val updatedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
            val appointmentDate = it.apointmnet_date

            appointmentDate == today && updatedTime > currentTime
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        val upcomingAppointment = todayData.minByOrNull {
            val appointmentDateTime = sdf.parse("${it.apointmnet_date} ${it.slot_time}")
            val calendar = Calendar.getInstance()
            calendar.time = appointmentDateTime
            calendar.add(Calendar.MINUTE, 30) // Add 30 minutes
            calendar.time
        }

        if (upcomingAppointment != null) {
            binding.liBookSession.visibility = View.GONE
            binding.liJoinSession.visibility = View.VISIBLE
            binding.txtTHName.text = "${upcomingAppointment.doctor_name}"
            binding.txtSlotTime.text = "Today at ${upcomingAppointment.slot_time}"
        } else {
            binding.liBookSession.visibility = View.VISIBLE
            binding.liJoinSession.visibility = View.GONE
        }
    }
}