package com.devative.littledoor.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityOtpverificationBinding
import com.devative.littledoor.util.Progress
import com.devative.littledoor.util.Utility
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class OTPVerificationActivity : BaseActivity() {
    lateinit var binding: ActivityOtpverificationBinding
    lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpverificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        progress = Progress(this)
        title = ""
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        viewModel = MainViewModel.getViewModel(this)
        binding.btnNext.setOnClickListener {
            if (binding.pinview.value.isEmpty() || binding.pinview.value.length < 6){
                Toasty.error(applicationContext,"Please enter OTP").show()
            }else {
                intent.getStringExtra(Constants.PHONE_NO)
                    ?.let { it1 -> viewModel.getVerifyOTPPatientLogin(it1, binding.pinview.value) }
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
                        Utility.savePrefString(applicationContext,Constants.AUTH_TOKEN,it.data.api_token)
                        if (it.data.basic_details == null)
                            startActivity(Intent(applicationContext, BasicDetailsForm::class.java))
                        else {
                            startActivity(Intent(applicationContext, MainActivity::class.java))
                            viewModel.insertUserData(it.data.basic_details)
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

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}