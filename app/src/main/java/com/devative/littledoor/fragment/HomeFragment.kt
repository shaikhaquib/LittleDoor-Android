package com.devative.littledoor.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.devative.littledoor.R
import com.devative.littledoor.activity.ProfilePicUploadActivity
import com.devative.littledoor.activity.UpdateProfile
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.HomeFragmentBinding
import com.devative.littledoor.model.UserDetails
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * Created by AQUIB RASHID SHAIKH on 20-03-2023.
 */
@AndroidEntryPoint
class HomeFragment  : Fragment() {
    private var basicDetails: UserDetails.Data? = null
    private lateinit var binding: HomeFragmentBinding
    private lateinit var vm: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dayGreetings()
        vm = MainViewModel.getViewModel(requireActivity())
        vm.fetchUserData()
        vm.basicDetails.observe(viewLifecycleOwner){
            if (!it.isNullOrEmpty()){
                basicDetails = it[0]
                updateUI()
            }
        }
        binding.imgProfile.setOnClickListener {
            startActivity(Intent(requireContext(),UpdateProfile::class.java))
        }
    }

    private fun updateUI() {
        binding.txtUserName.text = basicDetails?.name
        basicDetails?.image_url?.let { it1 -> binding.imgProfile.load(it1,R.drawable.profile_view)}
        binding.imgProfile.borderColor =
            ContextCompat.getColor(requireContext(), R.color.grey_primary)
        binding.imgProfile.borderWidth = 10
    }

    private fun dayGreetings() {
        val date = Date()
        val cal = Calendar.getInstance()
        cal.time = date
        val hour = cal[Calendar.HOUR_OF_DAY]

        //Set greeting

        //Set greeting
        when (hour) {
            in 12..16 -> {
                binding.txtGreet.text = "${getString(R.string.afternoon_greet)},"
            }
            in 17..20 -> {
                binding.txtGreet.text = "${getString(R.string.evening_greet)},"
            }
            in 21..23 -> {
                binding.txtGreet.text = "${getString(R.string.night_greet)},"
            }
            in 0..4 -> {
                binding.txtGreet.text = "${getString(R.string.night_greet)},"
            }
            else -> {
                binding.txtGreet.text = "${getString(R.string.morning_greet)},"
            }
        }
    }

}