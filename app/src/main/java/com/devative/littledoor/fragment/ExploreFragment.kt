package com.devative.littledoor.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devative.littledoor.R
import com.devative.littledoor.activity.MyPost
import com.devative.littledoor.activity.CreatePost
import com.devative.littledoor.adapter.ExplorerAdapter
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.ExploreViewModel
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ExploreFragmentBinding
import com.devative.littledoor.model.PostModel
import com.devative.littledoor.model.UserDetails
import com.devative.littledoor.util.DividerItemDecoration
import com.devative.littledoor.util.Progress
import com.devative.littledoor.util.Utility
import com.google.android.material.circularreveal.CircularRevealCompat
import java.lang.Math.hypot


/**
 * Created by AQUIB RASHID SHAIKH on 20-03-2023.
 */
class ExploreFragment : Fragment(),ExplorerAdapter.ExplorerAdapterEvent {
    private lateinit var binding: ExploreFragmentBinding
    private val vm :ExploreViewModel by activityViewModels()
    private val mainViewModel :MainViewModel by activityViewModels()
    private val postList = ArrayList<PostModel.Data>()
    private lateinit var adapter:ExplorerAdapter
    val progress: Progress by lazy {
        Progress(requireActivity() as AppCompatActivity)
    }
    var basicDetails: UserDetails.Data? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ExploreFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.fetchUserData()
        mainViewModel.basicDetails.observe(requireActivity()){
            if (!it.isNullOrEmpty()){
                basicDetails = it[0]
                basicDetails?.image_url?.let { it1 -> binding.imgProfile.load(it1,R.drawable.profile_view)}
            }
        }
        mainViewModel.getUserDetails()
        adapter = ExplorerAdapter(requireActivity(),postList,this)
        adapter.setHasStableIds(true)
        binding.rvTherapist.adapter = adapter
        binding.rvTherapist.addItemDecoration(DividerItemDecoration(requireContext(),LinearLayoutManager.VERTICAL,false))
        binding.imgProfile.setOnClickListener{
            startActivity(Intent(requireContext(),MyPost::class.java))
        }
        binding.fabCreatePost.setOnClickListener{
            startActivity(Intent(requireContext(),CreatePost::class.java))
        }
        observer()
    }

    private fun observer() {
        vm.getAllPost()
        vm.getAllPost.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    if (!progress.isShowing())
                        progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        postList.clear()
                        if (it.data.data.isNotEmpty()) {
                            postList.addAll(it.data.data as ArrayList)
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        Utility.errorToast(requireContext(), getString(R.string.some_thing_went_wrong))
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
        vm.likePost.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    if (!progress.isShowing())
                        progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        if (!it.data.status) {
                            Utility.errorToast(requireContext(), it.data.message)
                        }
                    } else {
                        Utility.errorToast(requireContext(), getString(R.string.some_thing_went_wrong))
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
                        if (it.data.status) {
                            Utility.successToast(requireContext(), it.data.message)
                        }else{
                            Utility.errorToast(requireContext(), it.data.message)
                        }
                    } else {
                        Utility.errorToast(requireContext(), getString(R.string.some_thing_went_wrong))
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

    override fun onclick(position: Int) {
    }

    override fun onLike(position: Int, isLike: Boolean, postID: Int) {
        val likeStatus = if (isLike) 1 else 0
        val map = HashMap<String,Any>()
        map["post_id"] = postID
        map["post_like"] = likeStatus
        vm.likePost(map)
    }

    override fun onComment(position: Int, postID: Int) {
        val dialog = CommentDialogFragment(
            postID
        )
        dialog.show(requireActivity().supportFragmentManager, "SingleSelectBottomSheetDialogFragment")

    }

    override fun onShare(position: Int, postID: Int) {
    }

}