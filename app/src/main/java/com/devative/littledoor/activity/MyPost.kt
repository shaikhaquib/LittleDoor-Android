package com.devative.littledoor.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devative.littledoor.R
import com.devative.littledoor.adapter.ExplorerAdapter
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.ExploreViewModel
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityMyPostBinding
import com.devative.littledoor.databinding.SettingsFragmentBinding
import com.devative.littledoor.fragment.CommentDialogFragment
import com.devative.littledoor.model.PostModel
import com.devative.littledoor.model.UserDetails
import com.devative.littledoor.util.DividerItemDecoration
import com.devative.littledoor.util.Utility
import com.google.android.material.tabs.TabLayout

class MyPost : BaseActivity(), OnClickListener, ExplorerAdapter.ExplorerAdapterEvent {

    private val binding: ActivityMyPostBinding by lazy {
        ActivityMyPostBinding.inflate(layoutInflater)
    }
    private val vm: ExploreViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private val userPosts = ArrayList<PostModel.Data>()
    private val myPostList = ArrayList<PostModel.Data>()
    private val myCommentPostList = ArrayList<PostModel.Data>()
    private val myLikePostList = ArrayList<PostModel.Data>()
    private lateinit var adapter: ExplorerAdapter
    private var filterCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor =
            ContextCompat.getColor(this, R.color.overlays_purple)

        binding.btnUpdateProfile.setOnClickListener {
            startActivity(Intent(this, UpdateProfile::class.java))
        }
        adapter = ExplorerAdapter(this, userPosts, this)
        adapter.setHasStableIds(true)
        binding.rvTherapist.adapter = adapter
        binding.rvTherapist.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL, false
            )
        )

        mainViewModel.fetchUserData()
        mainViewModel.basicDetails.observe(this) {
            if (!it.isNullOrEmpty()) {
                basicDetails = it[0]
                updateUI()
            }
        }

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val tabID = tab?.text.toString()
                if (tabID == getString(R.string.my_post) && filterCode != 1) {
                    filterCode = 1
                    if (myPostList.isEmpty()) {
                        vm.getAllPostUser()
                    } else {
                        updateList(myPostList)
                    }
                } else if (tabID == getString(R.string.comments) && filterCode != 2) {
                    filterCode = 2
                    if (myCommentPostList.isEmpty()) {
                        vm.getAllPostUserComment()
                    } else {
                        updateList(myCommentPostList)
                    }
                } else if (tabID == getString(R.string.likes) && filterCode != 3) {
                    filterCode = 3
                    if (myLikePostList.isEmpty()) {
                        vm.getAllPostUserLikes()
                    } else {
                        updateList(myLikePostList)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

    }

    private fun updateList(list: java.util.ArrayList<PostModel.Data>) {
        userPosts.clear()
        userPosts.addAll(list)
        adapter.notifyDataSetChanged()
        binding.rvTherapist.isVisible = userPosts.isNotEmpty()
        binding.emtyView.isVisible = userPosts.isEmpty()
    }

    private fun updateUI() {
        binding.txtName.text = basicDetails?.name
        binding.txtDesc.text = basicDetails?.email
        basicDetails?.image_url?.let { it1 ->
            binding.imgProfile.load(
                it1,
                R.drawable.profile_view
            )
        }
        binding.imgProfile.borderColor = ContextCompat.getColor(this, R.color.grey_primary)
        binding.imgProfile.borderWidth = 10
        observer()
    }

    override fun onClick(p0: View?) {

    }

    private fun observer() {
        vm.getAllPostUser()
        vm.getAllPostUserLikes()
        vm.getAllPostUserComment()
        vm.getAllPostUser.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    if (!progress.isShowing())
                        progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        myPostList.clear()
                        userPosts.clear()
                        if (it.data.data.isNotEmpty()) {
                            myPostList.addAll(it.data.data)
                            if (filterCode == 1) {
                                userPosts.clear()
                                userPosts.addAll(myPostList)
                                adapter.notifyDataSetChanged()
                            } else {
                                if (filterCode == 1) {
                                    userPosts.clear()
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                    } else {
                        Utility.errorToast(this, getString(R.string.some_thing_went_wrong))
                    }

                    binding.rvTherapist.isVisible = userPosts.isNotEmpty()
                    binding.emtyView.isVisible = userPosts.isEmpty()
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Utility.errorToast(
                            this,
                            it1
                        )
                    }
                }

            }
        }
        vm.getAllPostUserLikes.observe(this) {
            when (it.status) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        myLikePostList.clear()
                        if (it.data.data.isNotEmpty()) {
                            myLikePostList.addAll(it.data.data)
                            if (filterCode == 3) {
                                userPosts.clear()
                                userPosts.addAll(myLikePostList)
                                adapter.notifyDataSetChanged()
                            }
                        } else {
                            if (filterCode == 3) {
                                userPosts.clear()
                                adapter.notifyDataSetChanged()
                            }
                        }
                    } else {
                        Utility.errorToast(this, getString(R.string.some_thing_went_wrong))
                    }
                    binding.rvTherapist.isVisible = userPosts.isNotEmpty()
                    binding.emtyView.isVisible = userPosts.isEmpty()
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Utility.errorToast(
                            this,
                            it1
                        )
                    }
                }

            }
        }
        vm.getAllPostUserComments.observe(this) {
            when (it.status) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        myCommentPostList.clear()
                        if (it.data.data.isNotEmpty()) {
                            myCommentPostList.addAll(it.data.data)
                            if (filterCode == 2) {
                                userPosts.clear()
                                userPosts.addAll(myCommentPostList)
                                adapter.notifyDataSetChanged()
                            } else {
                                if (filterCode == 2) {
                                    userPosts.clear()
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                    } else {
                        Utility.errorToast(this, getString(R.string.some_thing_went_wrong))
                    }

                    binding.rvTherapist.isVisible = userPosts.isNotEmpty()
                    binding.emtyView.isVisible = userPosts.isEmpty()
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Utility.errorToast(
                            this,
                            it1
                        )
                    }
                }

            }
        }
        vm.likePost.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    if (!progress.isShowing())
                        progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        Utility.successToast(this, it.data.message)
                        vm.getAllPostUserLikes()
                    } else {
                        Utility.errorToast(this, getString(R.string.some_thing_went_wrong))
                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Utility.errorToast(
                            this,
                            it1
                        )
                    }
                }

            }
        }
        vm.addComment.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    if (!progress.isShowing())
                        progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        if (it.data.status) {
                            Utility.successToast(this, it.data.message)
                        } else {
                            Utility.errorToast(this, it.data.message)
                        }
                    } else {
                        Utility.errorToast(this, getString(R.string.some_thing_went_wrong))
                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Utility.errorToast(
                            this,
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
        val map = HashMap<String, Any>()
        map["post_id"] = postID
        map["post_like"] = likeStatus
        vm.likePost(map)
    }

    override fun onComment(position: Int, postID: Int) {
        val dialog = CommentDialogFragment(
            postID
        )
        dialog.show(supportFragmentManager, "SingleSelectBottomSheetDialogFragment")

    }

    override fun onShare(position: Int, postID: Int) {
    }

}