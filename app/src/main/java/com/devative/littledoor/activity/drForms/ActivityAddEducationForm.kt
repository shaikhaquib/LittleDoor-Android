package com.devative.littledoor.activity.drForms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import com.devative.littledoor.R
import com.devative.littledoor.databinding.ActivityAddEducationFormBinding
import com.devative.littledoor.util.IOSDatePickerFragment
import com.devative.littledoor.util.Logger

class ActivityAddEducationForm : AppCompatActivity(), OnClickListener {
    lateinit var binding: ActivityAddEducationFormBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEducationFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        sutUpClick()
    }

    private fun sutUpClick() {
        binding.btnStartDate.setOnClickListener(this)
        binding.btnEndDate.setOnClickListener(this)
        binding.llUploadFile.setOnClickListener(this)
        binding.txtRemove.setOnClickListener(this)
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

            binding.btnStartDate.id -> {
                openCalender(binding.btnStartDate)
            }
            binding.btnEndDate.id -> {
                openCalender(binding.btnEndDate)
            }
            binding.llUploadFile.id -> {
                FilePickerUtils.pickFileFromFileManager(this)
            }

            binding.txtRemove.id -> {
                binding.clFileLayout.visibility = View.GONE
            }
        }
    }

    private fun openCalender(btn: Button){
        val datePickerFragment = IOSDatePickerFragment.newInstance(0, "yyyy-MM-dd")
        datePickerFragment.setOnDateSelectedListener(object :
            IOSDatePickerFragment.OnDateSelectedListener {
            override fun onDateSelected(date: String) {
                // Handle selected date
                Logger.d("TAG", "onDateSelected: $date")
                btn.text = date
            }
        })
        datePickerFragment.show(supportFragmentManager, "date_picker")
    }

}