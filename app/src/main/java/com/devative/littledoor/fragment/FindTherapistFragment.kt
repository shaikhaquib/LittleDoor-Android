package com.devative.littledoor.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.devative.littledoor.ChatUi.ChatActivity
import com.devative.littledoor.R
import com.devative.littledoor.activity.BookAppointment
import com.devative.littledoor.activity.ThProfileDetails
import com.devative.littledoor.adapter.CategoryAdapter
import com.devative.littledoor.adapter.TherapistAdapter
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DrExperienceFormVM
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.FragmentFindTherapistBinding
import com.devative.littledoor.model.CategoryResponse
import com.devative.littledoor.model.DoctotorListRes
import com.devative.littledoor.util.Progress
import es.dmoral.toasty.Toasty

class FindTherapistFragment : Fragment(), TherapistAdapter.TherapistAdapterEvent {
    private lateinit var binding: FragmentFindTherapistBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val vm: DrExperienceFormVM by activityViewModels()
    private val doctorList = ArrayList<DoctotorListRes.Data>()
    private lateinit var adapter: TherapistAdapter
    private lateinit var progress: Progress
    private val categoryList = ArrayList<CategoryResponse.Data>()
    private lateinit var catAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindTherapistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TherapistAdapter(requireActivity(), doctorList, this)
        adapter.setHasStableIds(true)
        catAdapter = CategoryAdapter(requireActivity(), categoryList, object :
            CategoryAdapter.CategoryAdapterEvent {
            override fun onclick(position: Int) {

            }
        })
        catAdapter.setHasStableIds(true)
        binding.rvTherapist.adapter = adapter
        binding.rvCategory.adapter = catAdapter
        progress = Progress(requireActivity() as AppCompatActivity)
        processViewModel()
    }

    private fun processViewModel() {
        viewModel.getDoctorList()
        vm.getCategory()
        vm.categoryData.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    if (it.data?.status == true) {
                        categoryList.clear()
                        categoryList.addAll(it.data.data)
                        catAdapter.notifyDataSetChanged()
                    }
                }

                Status.ERROR -> {
                }

            }
        }
        viewModel.doctorList.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        doctorList.clear()
                        doctorList.addAll(it.data.data)
                        adapter.notifyDataSetChanged()
                    } else {
                        Toasty.error(requireContext(), getString(R.string.some_thing_went_wrong))
                            .show()
                    }
                    binding.rvTherapist.isVisible = doctorList.isNotEmpty()
                    binding.emtyView.isVisible = doctorList.isEmpty()
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            requireContext(),
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }

        }
    }

    override fun onclick(position: Int) {
        startActivity(
            Intent(requireContext(), ThProfileDetails::class.java)
                .putExtra(Constants.TH_DETAILS, doctorList[position])
        )
    }

    override fun bookAppointment(position: Int) {
        startActivity(
            Intent(requireContext(), BookAppointment::class.java)
                .putExtra(Constants.TH_DETAILS, doctorList[position])
        )
    }

    override fun onChat(position: Int) {
        startActivity(Intent(requireContext(), ChatActivity::class.java))
    }
}