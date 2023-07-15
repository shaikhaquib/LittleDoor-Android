package com.devative.littledoor.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityBasicDetailsFormBinding
import com.devative.littledoor.model.GetAllCitiesResponse
import com.devative.littledoor.model.SearchAbleList
import com.devative.littledoor.util.IOSDatePickerFragment
import com.devative.littledoor.util.Logger
import com.devative.littledoor.util.Progress
import com.devative.littledoor.util.SingleSelectBottomSheetDialogFragment
import com.devative.littledoor.util.Utility
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty


@AndroidEntryPoint
class BasicDetailsForm : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityBasicDetailsFormBinding
    lateinit var viewModel: MainViewModel
    var selectedCityId = -1
    var selectedDate = ""
    var selectedGender = ""
    val citiesList = ArrayList<GetAllCitiesResponse.Data>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasicDetailsFormBinding.inflate(layoutInflater)
        viewModel = MainViewModel.getViewModel(this)
        progress = Progress(this)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        init()
        setViewModel()

    }

    private fun init() {

        if (intent.hasExtra(Constants.IS_DOCTOR)) {
            binding.txtLabelMobile.visibility = View.VISIBLE
            binding.edtMobile.visibility = View.VISIBLE
        } else {
            binding.txtLabelMobile.visibility = View.GONE
            binding.edtMobile.visibility = View.GONE
        }

        binding.btnCitySelect.setOnClickListener(this)
        binding.btnCalender.setOnClickListener(this)
        binding.btnGender.setOnClickListener(this)
        binding.btnCreate.setOnClickListener(this)
        binding.txtTermsLabel.setOnClickListener(this)

    }

    private fun setViewModel() {
        viewModel.getCities()
        viewModel.getCities.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress?.show()
                }

                Status.SUCCESS -> {
                    progress?.dismiss()
                    if (it.data?.status == true) {
                        citiesList.addAll(it.data.data)
                        // Toasty.success(applicationContext, it.data.message).show()
                    } else {
                        Toasty.error(applicationContext, "No data found").show()
                    }
                }

                Status.ERROR -> {
                    progress?.dismiss()
                    it.message?.let { it1 ->
                        Toasty.error(
                            this,
                            it1, Toasty.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
        viewModel.createPatient.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress?.show()
                }

                Status.SUCCESS -> {
                    progress?.dismiss()
                    if (it.data?.status == true) {
                        Toasty.success(applicationContext, it.data.message).show()
                        startActivity(Intent(applicationContext, MCQActivity::class.java))
                        finish()
                    } else {
                        Toasty.error(applicationContext, it.data!!.message).show()
                    }
                }

                Status.ERROR -> {
                    progress?.dismiss()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnCitySelect.id -> {
                val items = ArrayList<SearchAbleList>()
                for (i in citiesList.indices) {
                    items.add(SearchAbleList(i, citiesList[i].city_name))
                }
                val dialog = SingleSelectBottomSheetDialogFragment(
                    this,
                    items,
                    "Select an option",
                    0,
                    true
                ) { selectedValue ->
                    Logger.d("TAG", "onCreate: $selectedValue")
                    binding.btnCitySelect.text = selectedValue.title
                    selectedCityId = citiesList[selectedValue.position].id
                    // Handle selected position here
                }
                dialog.show(supportFragmentManager, "SingleSelectBottomSheetDialogFragment")

            }

            binding.btnCalender.id -> {
                val datePickerFragment = IOSDatePickerFragment.newInstance(18, "yyyy-MM-dd")
                datePickerFragment.setOnDateSelectedListener(object :
                    IOSDatePickerFragment.OnDateSelectedListener {
                    override fun onDateSelected(date: String) {
                        // Handle selected date
                        Logger.d("TAG", "onDateSelected: $date")
                        binding.btnCalender.text = date
                        selectedDate = date
                    }
                })
                datePickerFragment.show(supportFragmentManager, "date_picker")

            }

            binding.btnGender.id -> {
                val items = arrayListOf<SearchAbleList>(
                    SearchAbleList(0, "Male"),
                    SearchAbleList(1, "Female"),
                    SearchAbleList(2, "Other")
                )
                val selectedValue = 0
                val dialog = SingleSelectBottomSheetDialogFragment(
                    this,
                    items,
                    "Select an option",
                    selectedValue
                ) { selected ->
                    Logger.d("TAG", "onCreate: $selected")
                    binding.btnGender.text = selected.title
                    selectedGender = items[selected.position].title

                }
                dialog.show(supportFragmentManager, "SingleSelectBottomSheetDialogFragment")

            }

            binding.btnCreate.id -> {
                validateForm()
            }

            binding.txtTermsLabel.id -> {
                startActivity(Intent(applicationContext, TermsAndCondition::class.java))
            }
        }
    }

    private fun validateForm() {
        when {
            !binding.chkTerms.isChecked -> {
                Utility.errorToast(
                    applicationContext,
                    getString(R.string.error_message_terms)
                )
                binding.chkTerms.requestFocus()
            }

            binding.edtName.text.toString().isEmpty() -> {
                binding.edtName.error = getString(R.string.error_empty_name)
                Utility.errorToast(applicationContext, getString(R.string.error_empty_name))
            }

            binding.edtEmail.text.toString().isEmpty() -> {
                binding.edtEmail.error = getString(R.string.error_empty_email)
                Utility.errorToast(
                    applicationContext,
                    getString(R.string.error_empty_email)
                )
            }

            intent.hasExtra(Constants.IS_DOCTOR) && binding.edtMobile.text.toString()
                .isEmpty() -> {
                binding.edtEmail.error = getString(R.string.error_empty_email)
                Utility.errorToast(
                    applicationContext,
                    getString(R.string.error_empty_email)
                )
            }

            selectedDate.isEmpty() -> {
                binding.btnCalender.error = getString(R.string.error_empty_dob)
                Utility.errorToast(applicationContext, getString(R.string.error_empty_dob))
            }

            selectedGender.isEmpty() -> {
                binding.btnGender.error = getString(R.string.error_empty_gender)
                Utility.errorToast(
                    applicationContext,
                    getString(R.string.error_empty_gender)
                )
            }

            selectedCityId == -1 -> {
                binding.btnCitySelect.error = getString(R.string.error_empty_city)
                Utility.errorToast(applicationContext, getString(R.string.error_empty_city))
            }

            else -> {
                if (intent.hasExtra(Constants.IS_DOCTOR)) {
                    startActivity(Intent(applicationContext,DoctorRegistrationMaster::class.java))
                } else {
                    viewModel.createUser(
                        binding.edtName.text.toString(),
                        selectedGender,
                        selectedDate,
                        binding.edtEmail.text.toString(),
                        selectedCityId.toString()
                    )
                }
            }
        }
    }

}