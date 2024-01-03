package com.devative.littledoor.fragment

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.init
import com.devative.littledoor.R
import com.devative.littledoor.adapter.CommentsAdapter
import com.devative.littledoor.adapter.ExplorerAdapter
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.ExploreViewModel
import com.devative.littledoor.databinding.BottomSheetCommentsBinding
import com.devative.littledoor.databinding.BottomSheetSingleSelectBinding
import com.devative.littledoor.model.PostCommentModel
import com.devative.littledoor.model.PostModel
import com.devative.littledoor.model.SearchAbleList
import com.devative.littledoor.util.Progress
import com.devative.littledoor.util.Utility
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*
import kotlin.collections.HashMap


/**
 * Created by AQUIB RASHID SHAIKH on 11-03-2023.
 */


class CommentDialogFragment(
    private val postID: Int
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetCommentsBinding
    private val vm: ExploreViewModel by activityViewModels()
    private val commentList = ArrayList<PostCommentModel.Data>()
    private lateinit var adapter: CommentsAdapter
    val progress: Progress by lazy {
        Progress(requireActivity() as AppCompatActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        // Set up RecyclerView with adapter

        setUpSheet(bottomSheet)
        binding.imgClose.setOnClickListener {
            dismiss()
        }
        binding.btnChat.setOnClickListener {
            if (binding.edtChat.text.isNullOrEmpty()){
                binding.edtChat.error = "Please enter comment message"
            }else{
                val map =  HashMap<String,Any>()
                map["post_id"] = postID
                map["comments"] = binding.edtChat.text.toString()
                vm.addComment(map)
            }
        }
        binding.itemList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        adapter = CommentsAdapter(requireActivity(), commentList)
        adapter.setHasStableIds(true)
        binding.itemList.adapter = adapter
        observer()
    }

    private fun setUpSheet(bottomSheet: View) {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isFitToContents = false
        val layoutParams: ViewGroup.LayoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun observer() {
        getCommenst()
        vm.comments.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    if (!progress.isShowing())
                        progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        commentList.clear()
                        if (it.data.data.isNotEmpty()) {
                            commentList.addAll(it.data.data as ArrayList)
                            adapter.notifyDataSetChanged()
                            binding.txtError.isGone = commentList.size > 0
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
                            it1
                        )
                    }
                }

            }
        }
        vm.addComment.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    if (!progress.isShowing())
                        progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        binding.edtChat.setText("")
                        getCommenst()
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
                            it1
                        )
                    }
                }

            }
        }

    }

    private fun getCommenst() {
        val map = HashMap<String, Any>()
        map["post_id"] = postID
        vm.getComments(map)
    }


    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }


}
