package com.devative.littledoor.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import com.devative.littledoor.R
import com.devative.littledoor.adapter.UserTransactionListAdapter
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.TransactionViewModel
import com.devative.littledoor.databinding.ActivityRevenueBinding
import com.devative.littledoor.databinding.ActivityRevenueWithdrawalBinding
import com.devative.littledoor.util.ListSpacingDecoration
import com.devative.littledoor.util.NumericKeyboardView
import com.devative.littledoor.util.Utility
import es.dmoral.toasty.Toasty

class RevenueWithdrawalActivity : BaseActivity() {
    private val binding: ActivityRevenueWithdrawalBinding by lazy {
        ActivityRevenueWithdrawalBinding.inflate(layoutInflater)
    }
    private val trModel: TransactionViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.numericKeyboard.setOnTypeListener(object : NumericKeyboardView.OnTypeListener {
            override fun onType(text: String) {
                val currentText = binding.edtAmount.text.toString()
                val newText = currentText + text
                binding.edtAmount.text = newText
            }

            override fun onBackSpace() {
                val currentText = binding.edtAmount.text.toString()
                if (currentText.isNotEmpty()) {
                    val newText = currentText.substring(0, currentText.length - 1)
                    binding.edtAmount.text = newText
                }
            }
        })

        binding.numericKeyboard.setOnSubmitListener(object : NumericKeyboardView.OnSubmitListener {
            override fun onSubmit() {
                val amount = binding.edtAmount.text.toString()
                if (amount.isEmpty() || amount.toInt() < 1) {
                    Utility.errorToast(applicationContext, "Amount cannot be empty or zero.")
                } else {
                    val dataMap = HashMap<String, Any>()
                    dataMap["request_amount"] = amount
                    trModel.withdrawalRequest(dataMap)
                }
            }
        })

        setUPObserver()

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

    private fun setUPObserver() {
        trModel.withdrawalRequest.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    val intent = Intent(applicationContext, WithdrawalStatusActivity::class.java)
                    intent.apply {
                        putExtra(WithdrawalStatusActivity.STATUS_SUCCESS, it.data?.status)
                        putExtra(WithdrawalStatusActivity.TRANSACTION_ID, it.data?.message)
                        putExtra(WithdrawalStatusActivity.TRANSACTION_MESSAGE, it.data?.message)
                    }
                    startActivity(intent)
                    finish()
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

}