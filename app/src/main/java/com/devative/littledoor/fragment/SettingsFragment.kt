package com.devative.littledoor.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.devative.littledoor.R
import com.devative.littledoor.activity.SignUpActivity
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.SettingsFragmentBinding
import com.devative.littledoor.model.LoginModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by AQUIB RASHID SHAIKH on 20-03-2023.
 */
@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var basicDetails: LoginModel.BasicDetails ? = null
    private lateinit var binding: SettingsFragmentBinding
    private lateinit var vm:MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.overlays_purple)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = MainViewModel.getViewModel(requireActivity())
        vm.fetchUserData()
        vm.basicDetails.observe(viewLifecycleOwner){
            if (!it.isNullOrEmpty()){
                basicDetails = it[0]
                updateUI()
            }
        }

        binding.txtSignOut.setOnClickListener {
            vm.deleteUserData()
            startActivity(Intent(requireContext(),SignUpActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun updateUI() {
        binding.txtName.text = basicDetails?.name
        binding.txtDesc.text = basicDetails?.email
    }

}