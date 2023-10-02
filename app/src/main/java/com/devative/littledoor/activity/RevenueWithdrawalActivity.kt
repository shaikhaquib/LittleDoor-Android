package com.devative.littledoor.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.devative.littledoor.R
import com.devative.littledoor.adapter.UserTransactionListAdapter
import com.devative.littledoor.databinding.ActivityRevenueBinding
import com.devative.littledoor.databinding.ActivityRevenueWithdrawalBinding
import com.devative.littledoor.util.ListSpacingDecoration
import com.devative.littledoor.util.NumericKeyboardView

class RevenueWithdrawalActivity : AppCompatActivity() {
    private val binding: ActivityRevenueWithdrawalBinding by lazy {
        ActivityRevenueWithdrawalBinding.inflate(layoutInflater)
    }
    private lateinit var adapter: UserTransactionListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.numericKeyboard.setOnTypeListener(object : NumericKeyboardView.OnTypeListener {
            override fun onType(text: String) {
                val currentText =  binding.edtAmount.text.toString()?:""
                val newText = currentText + text
                binding.edtAmount.setText(newText)
            }

            override fun onBackSpace() {
                val currentText = binding.edtAmount.text.toString()
                if (currentText.isNotEmpty()) {
                    val newText = currentText.substring(0, currentText.length - 1)
                    binding.edtAmount.setText(newText)
                }
            }
        })

        binding.numericKeyboard.setOnSubmitListener(object : NumericKeyboardView.OnSubmitListener {
            override fun onSubmit() {
                // Handle the submission event here
            }
        })

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