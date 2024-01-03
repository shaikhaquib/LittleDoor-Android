package com.devative.littledoor.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import com.devative.littledoor.R
import com.devative.littledoor.adapter.BankListAdapter
import com.devative.littledoor.adapter.UserTransactionListAdapter
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.BankViewModel
import com.devative.littledoor.architecturalComponents.viewmodel.TransactionViewModel
import com.devative.littledoor.databinding.ActivityRevenueBinding
import com.devative.littledoor.databinding.ActivitySelectBankBinding
import com.devative.littledoor.model.BankDetailsModel
import com.devative.littledoor.model.DoctorTransactionRes
import com.devative.littledoor.model.RevenueDetails
import com.devative.littledoor.util.GeneralBottomSheetDialog
import com.devative.littledoor.util.ListSpacingDecoration
import com.devative.littledoor.util.Utility
import com.google.android.material.tabs.TabLayout
import es.dmoral.toasty.Toasty

class RevenueActivity : BaseActivity() {
    private val binding: ActivityRevenueBinding by lazy {
        ActivityRevenueBinding.inflate(layoutInflater)
    }
    private val trViewModel: TransactionViewModel by viewModels()
    private val bankViewModel: BankViewModel by viewModels()
    private val list = ArrayList<DoctorTransactionRes.Data>()
    private val tempList = ArrayList<DoctorTransactionRes.Data>()
    private lateinit var adapter: UserTransactionListAdapter
    private var filterCode = 1
    private var docRes: RevenueDetails? = null
    private var bankDetails: BankDetailsModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.rvTransaction.addItemDecoration(ListSpacingDecoration())
        adapter = UserTransactionListAdapter(this, tempList, object :
            UserTransactionListAdapter.UserTransactionListAdapterEvent {
            override fun onclick(position: Int) {

            }
        })
        binding.rvTransaction.adapter = adapter
        binding.brnWithdraw.setOnClickListener {
            if (docRes?.data?.total_pending_amount ?: 0 == 0) {
                Utility.errorToast(applicationContext, "Insufficient balance")
            } else if (bankDetails?.data.isNullOrEmpty()) {
                val bottomSheetDialog = GeneralBottomSheetDialog()
                bottomSheetDialog.setTitle("Add Bank Details")
                bottomSheetDialog.setMessage("Bank details are missing. Please provide your bank details in order to proceed.")
                bottomSheetDialog.setNegativeButton("cancel") {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.setPositiveButton("continue") {
                    startActivity(
                        Intent(applicationContext, AddBankActivity::class.java).apply {
                            if (bankDetails != null && !bankDetails?.data.isNullOrEmpty()) {
                                putExtra("BankData", bankDetails)
                            }
                        }
                    )
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show(supportFragmentManager, "BottomSheetDialog")
            } else {
                startActivity(
                    Intent(applicationContext, RevenueWithdrawalActivity::class.java)
                )
            }
        }

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val tabID = tab?.text.toString()
                if (tabID == getString(R.string.transactions) && filterCode != 1) {
                    filterCode = 1
                    tempList.clear()
                    tempList.addAll(list)
                    adapter.notifyDataSetChanged()
                } else if (tabID == getString(R.string.pending) && filterCode != 2) {
                    filterCode = 2
                    tempList.clear()
                    val list = docRes?.data?.pending_request_amount_data
                    if (!list.isNullOrEmpty()) {
                        for (item in list) {
                            tempList.add(
                                DoctorTransactionRes.Data(
                                    amount = item.request_amount,
                                    status = "pending",
                                    created_at = item.created_at,
                                    transaction_number = item.transaction_number,
                                    doctor_id = 0,
                                    doctor_image = basicDetails?.image_url ?: "",
                                    doctor_name = "Withdrawal request",
                                    id = item.id,
                                    patient_id = 0,
                                    patient_image = basicDetails?.image_url ?: "",
                                    patient_name = "Withdrawal request"
                                )
                            )
                        }
                    }
                    adapter.notifyDataSetChanged()

                }

                binding.rvTransaction.isVisible = tempList.isNotEmpty()
                binding.emptyState.isVisible = tempList.isEmpty()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

    //    observe()
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

    fun observe() {
        trViewModel.getDoctorTransaction()
        bankViewModel.getBankDetails()
        trViewModel.getRevenue()
        bankViewModel.bankDetails.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    bankDetails = it.data
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

        trViewModel.getRevenue.observe(this) {
            when (it.status) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        docRes = it.data
                        binding.txtAmount.text = it.data.data.total_pending_amount.toString()
                    }
                }

                Status.ERROR -> {
                }

            }
        }

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
                        tempList.clear()
                        if (it.data.data.isNotEmpty()) {
                            list.addAll(it.data.data as ArrayList)
                            tempList.addAll(list)
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        Utility.errorToast(
                            this,
                            getString(R.string.some_thing_went_wrong)
                        )
                    }
                    binding.rvTransaction.isVisible = list.isNotEmpty()
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
                    binding.rvTransaction.isVisible = tempList.isNotEmpty()
                    binding.emptyState.isVisible = tempList.isEmpty()
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        observe()
    }
}