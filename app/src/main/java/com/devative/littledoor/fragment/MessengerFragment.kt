package com.devative.littledoor.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devative.littledoor.ChatUi.ChatActivity
import com.devative.littledoor.adapter.ChatRoomAdapter
import com.devative.littledoor.databinding.MessengerFragmentBinding


/**
 * Created by AQUIB RASHID SHAIKH on 20-03-2023.
 */
class MessengerFragment  : Fragment() {
    private lateinit var binding: MessengerFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                startActivity(Intent(requireContext(), ChatActivity::class.java))
            }
        })

     //   binding.rvChatRoom.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL,false))
    }

}