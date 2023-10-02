package com.devative.littledoor.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import com.devative.littledoor.R
import com.devative.littledoor.adapter.BankListAdapter
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.BankViewModel
import com.devative.littledoor.architecturalComponents.viewmodel.DrRegViewModel
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityAddBankBinding
import com.devative.littledoor.databinding.ActivitySelectBankBinding
import com.devative.littledoor.model.BankDetailsModel
import com.devative.littledoor.model.SearchAbleList
import com.devative.littledoor.util.Logger
import com.devative.littledoor.util.SingleSelectBottomSheetDialogFragment
import com.devative.littledoor.util.Utility
import es.dmoral.toasty.Toasty

class AddBankActivity : BaseActivity() {
    private val binding: ActivityAddBankBinding by lazy {
        ActivityAddBankBinding.inflate(layoutInflater)
    }
    private val vm: BankViewModel by viewModels()
    private val bankList = ArrayList<BankDetailsModel.Data>()
    private var bankDetails: BankDetailsModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        observe()
        getIntentData()

        binding.btnSelectAccountType.setOnClickListener {

            val items = arrayListOf<SearchAbleList>(
                SearchAbleList(0, "Saving Account"),
                SearchAbleList(1, "Current Account"),
            )
            val selectedValue = 0
            val dialog = SingleSelectBottomSheetDialogFragment(
                this,
                items,
                "Select an option",
                selectedValue
            ) { selected ->
                Logger.d("TAG", "onCreate: $selected")
                binding.btnSelectAccountType.text = selected.title
            }
            dialog.show(supportFragmentManager, "SingleSelectBottomSheetDialogFragment")


        }

        binding.btnAddDetails.setOnClickListener {
            if (validateForm()) {
                /*   startActivity(Intent(applicationContext, WithdrawalStatusActivity::class.java))
                   finish()*/
                val bankName = binding.BankName.text.toString().trim()
                val accountHolderName = binding.edtAccountHolderName.text.toString().trim()
                val accountNumber = binding.edtAccountNumber.text.toString().trim()
                val ifscCode = binding.edtIFC.text.toString().trim()
                val accountType = binding.btnSelectAccountType.text.toString().trim()
                val detailsModel = BankDetailsModel.Data(
                    account_holder_name = accountHolderName,
                    account_number = accountNumber,
                    account_type = accountType,
                    branch_name = bankName,
                    "",
                    0,
                    ifsc_code = ifscCode
                )
                bankList.add(detailsModel)
                val bankMap = HashMap<String,Any>()
                for(pos in bankList.indices){
                    bankMap["account_type"] = bankList[pos].account_type
                    bankMap["account_number"] =  bankList[pos].account_number
                    bankMap["account_holder_name"] =  bankList[pos].account_holder_name
                    bankMap["ifsc_code"] =  bankList[pos].ifsc_code
                    bankMap["branch_name"] =  bankList[pos].branch_name
                }
                vm.addBankDetails(bankMap)
            }
        }
    }

    private fun getIntentData() {
        if (intent.hasExtra("BankData")) {
            bankDetails = intent.getSerializableExtra("BankData") as BankDetailsModel
            if (bankDetails != null && !bankDetails?.data.isNullOrEmpty()) {
                bankList.addAll(bankDetails!!.data)
                if (intent.hasExtra("EditPosition")) {
                    val editPosition = intent.getIntExtra("EditPosition", -1)
                    if (editPosition != -1) {
                        val data = bankList[editPosition]
                        bankList.removeAt(editPosition)
                        binding.BankName.text = data.branch_name
                        binding.edtAccountHolderName.text = data.account_holder_name
                        binding.edtAccountNumber.text = data.account_number
                        binding.edtConfirmAccountNumber.text = data.account_number
                        binding.edtIFC.text = data.ifsc_code
                    }
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        val bankName = binding.BankName.text.toString().trim()
        val accountHolderName = binding.edtAccountHolderName.text.toString().trim()
        val accountNumber = binding.edtAccountNumber.text.toString().trim()
        val confirmAccountNumber = binding.edtConfirmAccountNumber.text.toString().trim()
        val ifscCode = binding.edtIFC.text.toString().trim()

        if (bankName.isEmpty()) {
            Utility.errorToast(applicationContext, getString(R.string.error_empty_bank_name))
            return false
        } else if (accountHolderName.isEmpty()) {
            Utility.errorToast(
                applicationContext,
                getString(R.string.error_empty_account_holder_name)
            )
            return false
        } else if (accountNumber.isEmpty()) {
            Utility.errorToast(applicationContext, getString(R.string.error_empty_account_number))
            return false
        } else if (confirmAccountNumber.isEmpty()) {
            Utility.errorToast(
                applicationContext,
                getString(R.string.error_empty_confirm_account_number)
            )
            return false
        } else if (accountNumber != confirmAccountNumber) {
            Utility.errorToast(
                applicationContext,
                getString(R.string.error_account_number_mismatch)
            )
            return false
        } else if (ifscCode.isEmpty()) {
            Utility.errorToast(applicationContext, getString(R.string.error_empty_ifsc_code))
            return false
        }

        return true
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
        vm.addBankDetails.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        Utility.successToast(applicationContext, it.data.message)
                    } else {
                        Utility.errorToast(applicationContext, it.data!!.message)
                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
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


}