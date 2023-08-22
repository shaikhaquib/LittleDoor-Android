package com.devative.littledoor.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import com.devative.littledoor.R
import com.devative.littledoor.adapter.UserAppointmentListAdapter
import com.devative.littledoor.adapter.UserTransactionListAdapter
import com.devative.littledoor.architecturalComponents.viewmodel.DrRegViewModel
import com.devative.littledoor.architecturalComponents.viewmodel.UpdateSessionDetailsVM
import com.devative.littledoor.databinding.ActivityUserAppointmentBinding
import com.devative.littledoor.databinding.ActivityUserTransactionBinding
import com.devative.littledoor.util.ListSpacingDecoration

class UserTransactionActivity : BaseActivity() {
    private val binding: ActivityUserTransactionBinding by lazy {
        ActivityUserTransactionBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.rvAppointment.addItemDecoration(ListSpacingDecoration())
        binding.rvAppointment.adapter = UserTransactionListAdapter(this,object:
            UserTransactionListAdapter.UserTransactionListAdapterEvent{
            override fun onclick(position: Int) {

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