package com.devative.littledoor.fragment.drdashboard

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.devative.littledoor.R
import com.devative.littledoor.adapter.AppointmentAdapter
import com.devative.littledoor.adapter.ExplorerAdapter
import com.devative.littledoor.databinding.TherapistAppointmentFragmentBinding
import com.devative.littledoor.util.DividerItemDecoration


/**
 * Created by AQUIB RASHID SHAIKH on 20-03-2023.
 */
class TherapistAppointmentFragment : Fragment() {
    private lateinit var binding: TherapistAppointmentFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TherapistAppointmentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvAppointment.adapter = AppointmentAdapter(requireActivity(),object:
            AppointmentAdapter.AppointmentAdapterEvent {
            override fun onclick(position: Int) {

            }
        })
    }

}