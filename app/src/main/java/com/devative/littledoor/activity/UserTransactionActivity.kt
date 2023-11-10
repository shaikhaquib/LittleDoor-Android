package com.devative.littledoor.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.devative.littledoor.R
import com.devative.littledoor.adapter.UserTransactionListAdapter
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.TransactionViewModel
import com.devative.littledoor.databinding.ActivityUserTransactionBinding
import com.devative.littledoor.model.DoctorTransactionRes
import com.devative.littledoor.util.ListSpacingDecoration
import com.devative.littledoor.util.Utility

class UserTransactionActivity : BaseActivity() {
    private val binding: ActivityUserTransactionBinding by lazy {
        ActivityUserTransactionBinding.inflate(layoutInflater)
    }
    private val trViewModel: TransactionViewModel by viewModels()
    private val list = ArrayList<DoctorTransactionRes.Data>()
    private lateinit var adapter: UserTransactionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.rvAppointment.addItemDecoration(ListSpacingDecoration())
        adapter = UserTransactionListAdapter(this,list,object:
            UserTransactionListAdapter.UserTransactionListAdapterEvent{
            override fun onclick(position: Int) {

            }
        })
        binding.rvAppointment.adapter = adapter

        observe()
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
    fun observe(){
        trViewModel.getDoctorTransaction()
        trViewModel.getDoctorTransaction.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    if (!progress.isShowing())
                        progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        list.clear()
                        if (it.data.data.isNotEmpty()) {
                            list.addAll(it.data.data as ArrayList)
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        Utility.errorToast(
                            this,
                            getString(R.string.some_thing_went_wrong)
                        )
                    }
                    binding.rvAppointment.isVisible = list.isNotEmpty()
                    binding.emptyState.isVisible = list.isEmpty()
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Utility.errorToast(
                            this,
                            getString(R.string.some_thing_went_wrong)
                        )
                    }
                }

            }
        }

    }

}