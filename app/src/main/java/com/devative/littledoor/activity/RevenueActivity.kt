package com.devative.littledoor.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.devative.littledoor.R
import com.devative.littledoor.adapter.BankListAdapter
import com.devative.littledoor.adapter.UserTransactionListAdapter
import com.devative.littledoor.databinding.ActivityRevenueBinding
import com.devative.littledoor.databinding.ActivitySelectBankBinding
import com.devative.littledoor.util.ListSpacingDecoration

class RevenueActivity : AppCompatActivity() {
    private val binding: ActivityRevenueBinding by lazy {
        ActivityRevenueBinding.inflate(layoutInflater)
    }
    private lateinit var adapter: UserTransactionListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.rvTransaction.addItemDecoration(ListSpacingDecoration())
        adapter = UserTransactionListAdapter(this,object:
            UserTransactionListAdapter.UserTransactionListAdapterEvent{
            override fun onclick(position: Int) {

            }
        })
        binding.rvTransaction.adapter = adapter
        binding.brnWithdraw.setOnClickListener {
            startActivity(
                Intent(applicationContext,RevenueWithdrawalActivity::class.java)
            )
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