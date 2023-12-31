package com.devative.littledoor.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DrRegViewModel
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityOtpverificationBinding
import com.devative.littledoor.util.Utility
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import java.util.concurrent.TimeUnit

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
        timer()
        viewModel = MainViewModel.getViewModel(this)
        binding.btnNext.setOnClickListener {
            if (binding.pinview.value.isEmpty()) {
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

        binding.txtResend.setOnClickListener {
            viewModel.getOTPPatientLogin(intent.getStringExtra(Constants.PHONE_NO).toString(), "1")
            timer()
        }
        viewModel.verifyOtp.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        Toasty.success(applicationContext, it.data.message).show()
                        Utility.savePrefString(
                            applicationContext,
                            Constants.AUTH_TOKEN,
                            it.data.api_token
                        )
                        if (it.data.user_details == null)
                            startActivity(BasicDetailsForm::class.java)
                        else {
                            viewModel.getUserDetails()
                        }
                        finish()
                    } else {
                        Toasty.error(applicationContext, it.data!!.message).show()
                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            getString(R.string.some_thing_went_wrong), Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
        viewModel.userDetails.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        it.data.let { data ->
                            // Toasty.success(applicationContext, it.data.message).show()
                            if (isDoctor || data.data.doctor_id != null) {
                                if (it.data.data.status == 1){
                                    startActivity(MainActivity::class.java,true)
                                }else {
                                    startActivity(DoctorRegistrationMaster::class.java)
                                }
                            } else {
                                startActivity(MainActivity::class.java)
                            }

                        }
                    } else {
                        Toasty.error(applicationContext,getString(R.string.some_thing_went_wrong)).show()
                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
/*
                        Toasty.error(
                            this,
                            getString(R.string.some_thing_went_wrong), Toasty.LENGTH_SHORT
                        ).show()
*/
                    }
                }

            }
        }
        drRegViewModel.verifyOtpTher.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
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
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            getString(R.string.some_thing_went_wrong), Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
        viewModel.OTPSend.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        if (it.data.otp != null) {
                            Utility.showNotification(
                                applicationContext,
                                "Use this OTP to Login:${it.data.otp}",
                                "Login Auth"
                            )
                            it.message?.let { it1 ->
                                Toasty.success(
                                    this,
                                    it1, Toasty.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            it.message?.let { it1 ->
                                Toasty.error(
                                    this,
                                    it1, Toasty.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            getString(R.string.some_thing_went_wrong), Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }


    }

    private fun timer(){
        val timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                )
                binding.txtTimer.text = "${timeLeft}s"
                binding.txtResend.isEnabled = false
                if (!binding.txtTimer.isVisible){
                    binding.txtTimer.isVisible = true
                }
            }

            override fun onFinish() {
                binding.txtResend.isEnabled = true
                if (binding.txtTimer.isVisible){
                    binding.txtTimer.isVisible = false
                }
            }
        }

        timer.start()
    }
    private fun startActivity(activity: Class<*>,isDoctor:Boolean = false) {
        val intent = Intent(this.applicationContext, activity)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        if (isDoctor){
            intent.putExtra(Constants.IS_DOCTOR,true)
        }
        startActivity(intent)
        finish()
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