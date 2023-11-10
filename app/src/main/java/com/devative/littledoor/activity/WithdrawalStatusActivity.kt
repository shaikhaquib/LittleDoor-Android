package com.devative.littledoor.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.devative.littledoor.R
import com.devative.littledoor.databinding.ActivityWithrawalStatusBinding

class WithdrawalStatusActivity : AppCompatActivity() {
    private val binding:ActivityWithrawalStatusBinding by lazy {
        ActivityWithrawalStatusBinding.inflate(layoutInflater)
    }
    companion object{
        const val STATUS_SUCCESS = "success_status"
        const val TRANSACTION_ID = "TRANSACTION_ID"
        const val TRANSACTION_MESSAGE = "TRANSACTION_MESSAGE"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val status  =  intent.getBooleanExtra(STATUS_SUCCESS,false)
        val transactionId  =  intent.getStringExtra(TRANSACTION_ID,)
        val transactionMessage  =  intent.getStringExtra(TRANSACTION_MESSAGE)

        if (status)
            binding.animationView.setAnimation(R.raw.success)
        else
            binding.animationView.setAnimation(R.raw.failed)


        binding.txtnMessage.text = transactionMessage
     //   binding.txtTransactionID.text = "Transaction ID: $transactionId"


        binding.materialButton.setOnClickListener{
            finish()
        }
    }
}