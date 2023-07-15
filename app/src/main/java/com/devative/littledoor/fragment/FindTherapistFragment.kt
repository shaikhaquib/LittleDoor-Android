package com.devative.littledoor.fragment

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.devative.littledoor.R
import com.devative.littledoor.adapter.ChatRoomAdapter
import com.devative.littledoor.adapter.TherapistAdapter
import com.devative.littledoor.databinding.FragmentFindTherapistBinding

class FindTherapistFragment : Fragment() {
    lateinit var binding:FragmentFindTherapistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.overlays_purple)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindTherapistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvTherapist.adapter = TherapistAdapter(requireActivity(),object :
            TherapistAdapter.TherapistAdapterEvent {
            override fun onclick(position: Int) {
            }
        })
    }
}