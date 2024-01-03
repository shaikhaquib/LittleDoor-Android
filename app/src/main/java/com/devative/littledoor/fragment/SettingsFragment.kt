package com.devative.littledoor.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.View.OnClickListener
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.devative.littledoor.R
import com.devative.littledoor.activity.AboutUs
import com.devative.littledoor.activity.DoctorRegistrationMaster
import com.devative.littledoor.activity.RevenueActivity
import com.devative.littledoor.activity.SelectBankActivity
import com.devative.littledoor.activity.SignUpActivity
import com.devative.littledoor.activity.THAddSessionDetailsActivity
import com.devative.littledoor.activity.TermsAndCondition
import com.devative.littledoor.activity.UpdateProfile
import com.devative.littledoor.activity.UserAppointmentActivity
import com.devative.littledoor.activity.UserNotificationActivity
import com.devative.littledoor.activity.UserTransactionActivity
import com.devative.littledoor.adapter.DoctorDetailsAdapter
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.SettingsFragmentBinding
import com.devative.littledoor.model.LoginModel
import com.devative.littledoor.model.UserDetails
import com.devative.littledoor.util.Utility
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by AQUIB RASHID SHAIKH on 20-03-2023.
 */
@AndroidEntryPoint
class SettingsFragment : Fragment(), OnClickListener {
    private var basicDetails: UserDetails.Data? = null
    private lateinit var binding: SettingsFragmentBinding
    private lateinit var vm: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.overlays_purple)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = MainViewModel.getViewModel(requireActivity())
        vm.fetchUserData()
        vm.basicDetails.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                basicDetails = it[0]
                updateUI()
                if (basicDetails?.status != 1){
                    binding.txtRevenue.isEnabled = false
                }
            }
        }

        binding.txtSignOut.setOnClickListener {
            vm.deleteUserData()
            Utility.clearPreference(requireContext())
            startActivity(Intent(requireContext(), SignUpActivity::class.java))
            requireActivity().finish()
        }
        binding.btnUpdateProfile.setOnClickListener {
            startActivity(Intent(requireContext(), UpdateProfile::class.java))
        }

        binding.lilTHOption.isVisible = Constants.isDoctor
        binding.lilUserOption.isGone = Constants.isDoctor

        binding.txtAppointment.setOnClickListener(this)
        binding.txtNotification.setOnClickListener(this)
        binding.txtTHNotification.setOnClickListener(this)
        binding.txtTransaction.setOnClickListener(this)
        binding.termsConditions.setOnClickListener(this)
        binding.privacyPolicy.setOnClickListener(this)
        binding.txtSessionDetails.setOnClickListener(this)
        binding.txtYourInformation.setOnClickListener(this)
        binding.txtBankDetails.setOnClickListener(this)
        binding.txtRevenue.setOnClickListener(this)
        binding.aboutUs.setOnClickListener(this)
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
        binding.imgProfile.borderColor =
            ContextCompat.getColor(requireContext(), R.color.grey_primary)
        binding.imgProfile.borderWidth = 10
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.txtAppointment.id -> startActivity(
                Intent(
                    requireContext(),
                    UserAppointmentActivity::class.java
                )
            )

            binding.txtTransaction.id -> startActivity(
                Intent(
                    requireContext(),
                    UserTransactionActivity::class.java
                )
            )

            binding.txtNotification.id -> startActivity(
                Intent(
                    requireContext(),
                    UserNotificationActivity::class.java
                )
            )

            binding.txtTHNotification.id -> startActivity(
                Intent(
                    requireContext(),
                    UserNotificationActivity::class.java
                )
            )
            binding.termsConditions.id -> startActivity(
                Intent(
                    requireContext(),
                    TermsAndCondition::class.java
                )
            )
            binding.privacyPolicy.id -> startActivity(
                Intent(
                    requireContext(),
                    TermsAndCondition::class.java
                ).putExtra("privacyPolicy",true)
            )
            binding.aboutUs.id -> startActivity(
                Intent(
                    requireContext(),
                    AboutUs::class.java
                )
            )
            binding.txtSessionDetails.id -> startActivity(
                Intent(
                    requireContext(),
                    THAddSessionDetailsActivity::class.java
                )
            )
            binding.txtYourInformation.id -> startActivity(
                Intent(
                    requireContext(),
                    DoctorRegistrationMaster::class.java
                ).putExtra(Constants.TH_REGISTERED,true)
            )
            binding.txtBankDetails.id -> startActivity(
                Intent(
                    requireContext(),
                    SelectBankActivity::class.java
                )
            )
            binding.txtRevenue.id -> startActivity(
                Intent(
                    requireContext(),
                    RevenueActivity::class.java
                )
            )
        }
    }

}