package com.devative.littledoor.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.transition.Fade
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Constants.PHONE_NO
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivitySignUpBinding
import com.devative.littledoor.util.Logger
import com.devative.littledoor.util.Progress
import com.devative.littledoor.util.Utility
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty


@AndroidEntryPoint
class SignUpActivity : BaseActivity(),View.OnClickListener {
    private lateinit var binding: ActivitySignUpBinding
    lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = MainViewModel.getViewModel(this)
        viewAnimation()
        setMessageWithClickableLink()
        binding.btnSendOTP.setOnClickListener(this)
        binding.btnTherapistLogin.setOnClickListener(this)
        Utility.clearPreference(this)

        viewModel.OTPSend.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress?.show()
                }

                Status.SUCCESS -> {
                    progress?.dismiss()
                    if (it.data?.status == true) {
                        if (it.data?.otp != null) {
                            Utility.showNotification(
                                applicationContext,
                                "Use this OTP to Login:${it.data?.otp}",
                                "Login Auth"
                            )
                            it.message?.let { it1 ->
                                Toasty.success(
                                    this,
                                    it1, Toasty.LENGTH_SHORT
                                ).show()
                            }
                            startActivity(
                                Intent(
                                    applicationContext,
                                    OTPVerificationActivity::class.java
                                ).putExtra(PHONE_NO, binding.edtPhoneNo.text.toString())
                            )
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

    private fun viewAnimation() {
        val fade = Fade()
        window.decorView
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)
        window.enterTransition = fade
        window.exitTransition = fade
    }

    private fun setMessageWithClickableLink() {
        val spannableString = SpannableString(getString(R.string.by_sign_up_))
        Logger.d("TAG", "setMessageWithClickableLink: ${spannableString.length}")
        val clickableSpan1 = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = ContextCompat.getColor(applicationContext, R.color.secondary)
            }

            override fun onClick(view: View) {
                Toast.makeText(this@SignUpActivity, "Clicked Privacy", Toast.LENGTH_SHORT).show()
            }
        }
        val startIndex = spannableString.indexOf("Terms and Conditions")
        val endIndex = startIndex + "Terms and Conditions".length
        spannableString.setSpan(
            clickableSpan1, startIndex, endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val clickableSpan2 = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = ContextCompat.getColor(applicationContext, R.color.secondary)
            }

            override fun onClick(view: View) {
                Toast.makeText(this@SignUpActivity, "Clicked Conditions", Toast.LENGTH_SHORT).show()
            }
        }
        val startIndexspan2 = spannableString.indexOf("Privacy Policy")
        val endIndexspan2 = startIndexspan2 + "Privacy Policy".length
        spannableString.setSpan(
            clickableSpan2, startIndexspan2, endIndexspan2,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        /* binding.termsText.movementMethod =
             LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
         binding.termsText.setText(spannableString, TextView.BufferType.SPANNABLE)*/
    }

    override fun onClick(v: View?) {
        when (v?.id){
            binding.btnSendOTP.id ->{
                if (binding.edtPhoneNo.text.isNullOrEmpty()) {
                    binding.edtPhoneNo.error = getString(R.string.phone_error_empty)
                    Toasty.error(applicationContext, getString(R.string.phone_error_empty)).show()
                } else {
                    viewModel.getOTPPatientLogin(binding.edtPhoneNo.text.toString(), "1")
                }
            }
            binding.btnTherapistLogin.id->{
                startActivity(Intent(applicationContext, BasicDetailsForm::class.java).putExtra(Constants.IS_DOCTOR,true))
            }
        }
    }


}