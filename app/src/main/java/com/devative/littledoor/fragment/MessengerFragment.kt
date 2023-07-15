package com.devative.littledoor.fragment

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.devative.littledoor.R
import com.devative.littledoor.adapter.ChatRoomAdapter
import com.devative.littledoor.adapter.TherapistAdapter
import com.devative.littledoor.databinding.MessengerFragmentBinding
import com.devative.littledoor.util.DividerItemDecoration


/**
 * Created by AQUIB RASHID SHAIKH on 20-03-2023.
 */
class MessengerFragment  : Fragment() {
    private lateinit var binding: MessengerFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.overlays_purple)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MessengerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvChatRoom.adapter = ChatRoomAdapter(requireActivity(),object :
            ChatRoomAdapter.ChatRoomAdapterEvent {
            override fun onclick(position: Int) {
            }
        })

     //   binding.rvChatRoom.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL,false))
    }

}