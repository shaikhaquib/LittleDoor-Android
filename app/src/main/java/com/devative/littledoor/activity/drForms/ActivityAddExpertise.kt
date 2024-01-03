package com.devative.littledoor.activity.drForms

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.devative.littledoor.R
import com.devative.littledoor.activity.BaseActivity
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.DrRegViewModel
import com.devative.littledoor.databinding.ActivityAddExpertiseBinding
import com.devative.littledoor.model.DoctorDetailsResponse
import com.devative.littledoor.util.Progress
import com.devative.littledoor.util.Utility
import com.google.android.material.chip.Chip
import es.dmoral.toasty.Toasty

class ActivityAddExpertise : BaseActivity() {
    lateinit var binding: ActivityAddExpertiseBinding
    private val itemList = mutableListOf<String>()
    private val vmDR: DrRegViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpertiseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        
        initIntent()
        observe()
        uiClick()
    }

    private fun uiClick() {
        binding.addButton.setOnClickListener {
            val enteredItem = binding.autoCompleteTextView.text.toString()
            if (enteredItem.isNotEmpty() && !itemList.contains(enteredItem)) {
                createChip(enteredItem)
                binding.autoCompleteTextView.text.clear()
            }
        }
        binding.btnCreate.setOnClickListener {
            if (itemList.isEmpty()) {
                Toasty.error(applicationContext, "Please add at-least one skill").show()
            } else {
                val fileMap = HashMap<String, Uri>()
                val dataMap = HashMap<String, String>()
                dataMap["step"] = "3"
                for (i in itemList.indices) {
                    dataMap["skills[$i]"] = itemList[i]
                }
                vmDR.uploadData(
                    this,
                    fileMap,
                    dataMap
                )
            }
        }

        binding.autoCompleteTextView.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val enteredItem = binding.autoCompleteTextView.text.toString()
                if (enteredItem.isNotEmpty() && !itemList.contains(enteredItem)) {
                    createChip(enteredItem)
                }
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun observe() {
        vmDR.getSkill()
        vmDR.skillData.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    it.data?.data?.let { skills ->
                        val autoCompleteList = ArrayList<String>()
                        for (skill in skills) {
                            autoCompleteList.add(skill.name)
                        }
                        val adapter =
                            ArrayAdapter(
                                this,
                                android.R.layout.simple_dropdown_item_1line,
                                autoCompleteList
                            )
                        binding.autoCompleteTextView.setAdapter(adapter)
                        binding.autoCompleteTextView.dropDownAnchor = binding.linearLayout4.id
                        binding.autoCompleteTextView.onItemClickListener =
                            AdapterView.OnItemClickListener { parent, _, position, _ ->
                                val selectedItem = parent.getItemAtPosition(position).toString()
                                createChip(selectedItem)
                            }
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

        vmDR.uploadResponse.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        Toasty.success(applicationContext, it.data.message).show()
                        finish()
                    } else {
                        Toasty.error(applicationContext, it.data!!.message).show()
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

    private fun initIntent() {
        if (intent.hasExtra(Constants.FORM_EDIT_DATA)) {
            if (intent.hasExtra(Constants.FORM_EDIT_DATA)) {
                val data =
                    intent.getSerializableExtra(Constants.FORM_EDIT_DATA) as DoctorDetailsResponse.Data
                for (skill in data.skills) {
                    itemList.add(skill.skill_name)
                    createChip(skill.skill_name)
                }
            }
        }
    }

    private fun createChip(text: String) {
        // Check if the item is already in the list
        if (!itemList.contains(text)) {
            itemList.add(text)
        }
        binding.chipGroup.addView(createTagChip(this, text))
    }

    private fun createTagChip(context: Context, chipName: String): Chip {
        binding.autoCompleteTextView.text.clear()
        return Chip(context).apply {
            text = chipName
            setBackgroundColor(ContextCompat.getColor(context, R.color.chipback))
            isCloseIconVisible = true
            setTextColor(ContextCompat.getColor(context, R.color.black))
            closeIcon = ContextCompat.getDrawable(applicationContext, R.drawable.cancel)
            setTextAppearance(R.style.TextTitleNormal_12sp)
            chipCornerRadius = 15f
            setPadding(24)
            setOnCloseIconClickListener {
                binding.chipGroup.removeView(this)
                itemList.remove(text)
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

}



