package com.devative.littledoor.fragment.drdashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.devative.littledoor.ChatUi.liveStreaming.LiveStreaming
import com.devative.littledoor.R
import com.devative.littledoor.adapter.AppointmentAdapter
import com.devative.littledoor.adapter.UserAppointmentListAdapter
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.TherapistAppointmentFragmentBinding
import com.devative.littledoor.model.UserAppointmentModel
import com.devative.littledoor.util.GeneralBottomSheetDialog
import com.devative.littledoor.util.Progress
import com.devative.littledoor.util.Utility
import com.google.android.material.tabs.TabLayout
import es.dmoral.toasty.Toasty


/**
 * Created by AQUIB RASHID SHAIKH on 20-03-2023.
 */
class TherapistAppointmentFragment : Fragment() {
    private lateinit var binding: TherapistAppointmentFragmentBinding
    private val vm: MainViewModel by activityViewModels()
    private val mainList = ArrayList<UserAppointmentModel.Data>()
    private val list = ArrayList<UserAppointmentModel.Data>()
    lateinit var adapter: AppointmentAdapter
    private var filterCode = 1
    val progress: Progress by lazy {
        Progress(requireActivity() as AppCompatActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TherapistAppointmentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AppointmentAdapter(requireActivity(), list, object :
            AppointmentAdapter.AppointmentAdapterEvent {
            override fun onclick(position: Int) {
                val appointment = list[position]
                val bottomSheetDialog = GeneralBottomSheetDialog()
                bottomSheetDialog.setTitle("User Appointment")
                bottomSheetDialog.setMessage(
                    "you have selected an appointment, Which ${
                        Constants.getTimeRemaining(
                            "${appointment.apointmnet_date}, ${appointment.slot_time}"
                        )
                    }.\n Do you wants join this call."
                )
                bottomSheetDialog.setNegativeButton("cancel") {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.setPositiveButton("Join") {
                    startActivity(
                        Intent(context, LiveStreaming::class.java)
                            .putExtra("CHANNEL_ID", appointment.id.toString())
                            .putExtra("IS_HOST", true)
                    )
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show(
                    requireActivity().supportFragmentManager,
                    "BottomSheetDialog"
                )
            }
        })
        adapter.setHasStableIds(true)
        binding.rvAppointment.adapter = adapter
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val tabID = tab?.text.toString()
                if (tabID == getString(R.string.today) && filterCode != 1) {
                    filterCode = 1
                    refreshList()
                } else if (tabID == getString(R.string.upcoming) && filterCode != 2) {
                    filterCode = 2
                    refreshList()
                } else if (tabID == getString(R.string.previous) && filterCode != 3) {
                    filterCode = 3
                    refreshList()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        observe()
    }

    private fun observe() {
        vm.getUserBookedAppointment()
        vm.getUserBookedAppointment.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    if (!progress.isShowing())
                        progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        mainList.clear()
                        if (it.data.data.isNotEmpty()) {
                            mainList.addAll(it.data.data as ArrayList)
                            refreshList()
                        }
                    } else {
                      /*  Utility.errorToast(
                            requireContext(),
                            getString(R.string.some_thing_went_wrong)
                        )*/
                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                       /* Utility.errorToast(
                            requireContext(),
                            getString(R.string.some_thing_went_wrong)
                        )*/
                    }
                }

            }
        }

    }

    fun refreshList() {
        list.clear()
        val l = Constants.filterAndSortData(mainList, filterCode)
        if (l.isNotEmpty()) {
            list.addAll(l)
        }
        adapter.notifyDataSetChanged()
        binding.noAppointmentError.isVisible = list.isEmpty()
        binding.rvAppointment.isVisible = list.isNotEmpty()

    }

}