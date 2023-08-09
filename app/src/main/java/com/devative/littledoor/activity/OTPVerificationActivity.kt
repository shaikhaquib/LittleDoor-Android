package com.devative.littledoor.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DrRegViewModel
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityOtpverificationBinding
import com.devative.littledoor.model.DrRegistrationMasterModel
import com.devative.littledoor.util.Progress
import com.devative.littledoor.util.Utility
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class OTPVerificationActivity : BaseActivity() {
    private lateinit var binding: ActivityOtpverificationBinding
    private lateinit var viewModel: MainViewModel
    private val drRegViewModel: DrRegViewModel by viewModels()

    private var isDoctor = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpverificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        
        title = ""
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        isDoctor = intent.hasExtra(Constants.IS_DOCTOR)

        viewModel = MainViewModel.getViewModel(this)
        binding.btnNext.setOnClickListener {
            if (binding.pinview.value.isEmpty() || binding.pinview.value.length < 6) {
                Toasty.error(applicationContext, "Please enter OTP").show()
            } else {
                intent.getStringExtra(Constants.PHONE_NO)
                    ?.let { it1 ->
                        if (isDoctor)
                            drRegViewModel.getVerifyOTPTherapistLogin(it1, binding.pinview.value)
                        else
                            viewModel.getVerifyOTPPatientLogin(it1, binding.pinview.value)
                    }
            }
        }
        viewModel.verifyOtp.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress?.show()
                }

                Status.SUCCESS -> {
                    progress?.dismiss()
                    if (it.data?.status == true) {
                        Toasty.success(applicationContext, it.data.message).show()
                        Utility.savePrefString(
                            applicationContext,
                            Constants.AUTH_TOKEN,
                            it.data.api_token
                        )
                        if (it.data.user_details == null)
                            startActivity(Intent(applicationContext, BasicDetailsForm::class.java))
                        else {
                            viewModel.getUserDetails()
                        }
                        finish()
                    } else {
                        Toasty.error(applicationContext, it.data!!.message).show()
                    }
                }

                Status.ERROR -> {
                    progress?.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
        viewModel.userDetails.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress?.show()
                }

                Status.SUCCESS -> {
                    progress?.dismiss()
                    if (it.data?.status == true) {
                        it.data?.let { data ->
                            // Toasty.success(applicationContext, it.data.message).show()
                            if (isDoctor || data.data.doctor_id != null) {
                                startActivity(
                                    Intent(
                                        applicationContext,
                                        DoctorRegistrationMaster::class.java
                                    )
                                )
                            } else {
                                startActivity(Intent(applicationContext, MainActivity::class.java))
                            }
                            finish()
                        }
                    } else {
                        Toasty.error(applicationContext,getString(R.string.some_thing_went_wrong)).show()
                    }
                }

                Status.ERROR -> {
                    progress?.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
        drRegViewModel.verifyOtpTher.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress?.show()
                }

                Status.SUCCESS -> {
                    progress?.dismiss()
                    if (it.data?.status == true) {
                        Toasty.success(applicationContext, it.data.message).show()
                        Utility.savePrefString(
                            applicationContext,
                            Constants.AUTH_TOKEN,
                            it.data.api_token
                        )
                       viewModel.getUserDetails()
                    } else {
                        Toasty.error(applicationContext, it.data!!.message).show()
                    }
                }

                Status.ERROR -> {
                    progress?.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                startActivity(Intent(applicationContext,SignUpActivity::class.java))
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}