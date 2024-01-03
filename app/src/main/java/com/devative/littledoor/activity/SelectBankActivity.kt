package com.devative.littledoor.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import com.devative.littledoor.R
import com.devative.littledoor.adapter.BankListAdapter
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.BankViewModel
import com.devative.littledoor.databinding.ActivitySelectBankBinding
import com.devative.littledoor.model.BankDetailsModel
import com.devative.littledoor.model.SearchAbleList
import com.devative.littledoor.util.ListSpacingDecoration
import com.devative.littledoor.util.SingleSelectBottomSheetDialogFragment
import com.devative.littledoor.util.Utility
import es.dmoral.toasty.Toasty

class SelectBankActivity : BaseActivity() {
    private val binding: ActivitySelectBankBinding by lazy {
        ActivitySelectBankBinding.inflate(layoutInflater)
    }
    private lateinit var adapter: BankListAdapter
    private val vm: BankViewModel by viewModels()
    private val bankList = ArrayList<BankDetailsModel.Data>()
    private var bankDetails:BankDetailsModel?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.rvBankList.addItemDecoration(ListSpacingDecoration())
        adapter = BankListAdapter(this, bankList, object :
            BankListAdapter.BankListAdapterEvent {
            override fun onclick(position: Int) {

            }

            override fun onclickMenu(position: Int) {
                moreOperation(position)
            }
        })
        binding.rvBankList.adapter = adapter
        binding.addNewBankAccount.setOnClickListener {
            startActivity(
                Intent(applicationContext, AddBankActivity::class.java).apply {
                    if (bankDetails != null && !bankDetails?.data.isNullOrEmpty()){
                        putExtra("BankData",bankDetails)
                    }
                }
            )
        }
    }

    private fun moreOperation(position: Int) {
        val items = arrayListOf(SearchAbleList(0, "Delete", R.drawable.delete),SearchAbleList(1, "Edit", R.drawable.edit))
        val dialog = SingleSelectBottomSheetDialogFragment(
            applicationContext,
            items,
            "Menu Option",
            cancelAble = true
        ) { selectedValue ->
            when (selectedValue.position) {
                0 -> vm.deleteBankDetails(bankList[position].id)
                1 -> {
                    startActivity(
                        Intent(applicationContext, AddBankActivity::class.java).apply {
                            if (bankDetails != null && !bankDetails?.data.isNullOrEmpty()) {
                                putExtra("BankData", bankDetails)
                                putExtra("EditPosition", position)
                            }
                        }
                    )
                }

            }
        }
        dialog.show(supportFragmentManager, "SearchAbleList")
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
        vm.getBankDetails()
        vm.bankDetails.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    bankDetails = it.data
                    if (it.data?.status == true) {
                        bankList.clear()
                        bankList.addAll(it.data.data)
                        adapter.notifyDataSetChanged()
                    }
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
        vm.deleteBankDetails.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }
                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        Utility.successToast(applicationContext,it.data.message)
                        vm.getBankDetails()
                    }else{
                        Utility.errorToast(applicationContext,it.data!!.message)
                    }
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

    override fun onResume() {
        super.onResume()
        observe()
    }
}