package com.devative.littledoor.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.devative.littledoor.R
import com.devative.littledoor.databinding.ActivityWithrawalStatusBinding

class WithdrawalStatusActivity : AppCompatActivity() {
    private val binding:ActivityWithrawalStatusBinding by lazy {
        ActivityWithrawalStatusBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.materialButton.setOnClickListener{
            finish()
        }
    }
}