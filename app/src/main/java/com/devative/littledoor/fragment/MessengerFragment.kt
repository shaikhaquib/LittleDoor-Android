package com.devative.littledoor.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.devative.littledoor.ChatUi.ChatActivity
import com.devative.littledoor.adapter.ChatRoomAdapter
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.MessengerFragmentBinding
import com.devative.littledoor.model.ChatListResponse
import com.devative.littledoor.util.Progress


/**
 * Created by AQUIB RASHID SHAIKH on 20-03-2023.
 */
class MessengerFragment : Fragment() {
    private lateinit var binding: MessengerFragmentBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val chatList = ArrayList<ChatListResponse.Data>()
    private val progress: Progress by lazy {
        Progress(requireActivity() as AppCompatActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MessengerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ChatRoomAdapter(requireActivity(), chatList, object :
            ChatRoomAdapter.ChatRoomAdapterEvent {
            override fun onclick(position: Int) {
                startActivity(Intent(requireContext(), ChatActivity::class.java).apply {
                    putExtra("data", chatList[position])
                })
            }
        })

        binding.rvChatRoom.adapter = adapter

        binding.edtSearch.onFocusChangeListener =
            View.OnFocusChangeListener { p0, p1 ->
                if (p1)
                    binding.appbar.setExpanded(false)
            }

        binding.edtSearch.doOnTextChanged { text, start, before, count ->
            if (text != null) {
                adapter.filter(binding.edtSearch.text.toString())
            }
        }

        viewModel.getChatList()
        observe()
        //   binding.rvChatRoom.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL,false))
    }

    fun observe() {
        viewModel.getChat.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        chatList.clear()
                        chatList.addAll(it.data.data)
                        binding.rvChatRoom.adapter?.notifyDataSetChanged()
                    }
                    binding.rvChatRoom.isVisible = chatList.isNotEmpty()
                    binding.emptyView.isVisible = chatList.isEmpty()
                }

                Status.ERROR -> {
                    progress.dismiss()
                    binding.rvChatRoom.isVisible = chatList.isNotEmpty()
                    binding.emptyView.isVisible = chatList.isEmpty()
                }

            }
        }

    }

}