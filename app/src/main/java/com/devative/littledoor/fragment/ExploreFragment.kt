package com.devative.littledoor.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devative.littledoor.R
import com.devative.littledoor.activity.MyPost
import com.devative.littledoor.activity.CreatePost
import com.devative.littledoor.adapter.ExplorerAdapter
import com.devative.littledoor.architecturalComponents.helper.Constants.isDoctor
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


/**
 * Created by AQUIB RASHID SHAIKH on 20-03-2023.
 */
class ExploreFragment : Fragment(), ExplorerAdapter.ExplorerAdapterEvent {
    private lateinit var binding: ExploreFragmentBinding
    private val vm: ExploreViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val postList = ArrayList<PostModel.Data>()
    private lateinit var adapter: ExplorerAdapter
    private val uniqueIds = HashSet<Int>()
    val progress: Progress by lazy {
        Progress(requireActivity() as AppCompatActivity)
    }
    var basicDetails: UserDetails.Data? = null
    var current_page: Int = 0
    var last_page: Int = 1
    val per_page: Int = 10
    var total: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ExploreFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.fetchUserData()
        mainViewModel.basicDetails.observe(requireActivity()) {
            if (!it.isNullOrEmpty()) {
                basicDetails = it[0]
                basicDetails?.image_url?.let { it1 ->
                    binding.imgProfile.load(
                        it1,
                        R.drawable.profile_view
                    )
                }
                if (isDoctor && basicDetails?.status != 1){
                    binding.fabCreatePost.visibility = View.GONE
                    binding.imgProfile.visibility = View.GONE
                }
            }
        }
        mainViewModel.getUserDetails()
        adapter = ExplorerAdapter(requireActivity(), postList, this)
        adapter.setHasStableIds(true)
        binding.rvTherapist.adapter = adapter
        binding.rvTherapist.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        )
        binding.imgProfile.setOnClickListener {
            startActivity(Intent(requireContext(), MyPost::class.java))
        }
        binding.fabCreatePost.setOnClickListener {
            startActivity(Intent(requireContext(), CreatePost::class.java))
        }
        adapter.setOnEndReachedListener {
            if (total > current_page)
                vm.getAllPost(current_page + 1, per_page)
        }
        observer()
    }

    private fun observer() {
        vm.getAllPost.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    if (!binding.animationView.isVisible) {
                        binding.animationView.isVisible = true
                    }
                }

                Status.SUCCESS -> {
                    if (binding.animationView.isVisible) {
                        binding.animationView.isVisible = false
                    }
                    if (it.data?.status == true) {
                        current_page = it.data.current_page
                        last_page = it.data.last_page
                        total = it.data.total

                        if (current_page == 0){

                        }

                        val position = if (postList.isEmpty()) 0 else postList.size - 1

                        //  per_page =  it.data.current_page
                        if (it.data.data.isNotEmpty()) {
                            for (data in it.data.data) {
                                if (uniqueIds.add(data.id)) {
                                    postList.add(data)
                                }
                            }
                            adapter.notifyItemInserted(position)
                        }
                    } else {
                        Utility.errorToast(
                            requireContext(),
                            getString(R.string.some_thing_went_wrong)
                        )
                    }
                    binding.rvTherapist.isVisible = postList.isNotEmpty()
                    binding.emtyView.isVisible = postList.isEmpty()
                }

                Status.ERROR -> {
                    if (binding.animationView.isVisible) {
                        binding.animationView.isVisible = false
                    }
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
                    if (!binding.animationView.isVisible) {
                        binding.animationView.isVisible = true
                    }
                }

                Status.SUCCESS -> {
                    if (binding.animationView.isVisible) {
                        binding.animationView.isVisible = false
                    }
                    if (it.data?.status == true) {
                        if (!it.data.status) {
                            Utility.errorToast(requireContext(), it.data.message)
                        }
                    } else {
                        Utility.errorToast(
                            requireContext(),
                            getString(R.string.some_thing_went_wrong)
                        )
                    }
                }

                Status.ERROR -> {
                    if (binding.animationView.isVisible) {
                        binding.animationView.isVisible = false
                    }
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
                        } else {
                            Utility.errorToast(requireContext(), it.data.message)
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
        dialog.show(
            requireActivity().supportFragmentManager,
            "SingleSelectBottomSheetDialogFragment"
        )

    }

    override fun onShare(position: Int, postID: PostModel.Data) {
    }

    override fun onResume() {
        super.onResume()
        current_page =  0
        vm.getAllPost(current_page, per_page)
    }
}