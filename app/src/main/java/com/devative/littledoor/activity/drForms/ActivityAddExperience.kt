package com.devative.littledoor.activity.drForms

import FilePickerUtils
import FilePickerUtils.getFileNameFromUri
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import carbon.widget.Button
import com.devative.littledoor.R
import com.devative.littledoor.databinding.ActivityAddExperienceBinding
import com.devative.littledoor.model.SearchAbleList
import com.devative.littledoor.util.SingleSelectBottomSheetDialogFragment

class ActivityAddExperience : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityAddExperienceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExperienceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        clickHandle()
    }

    private fun clickHandle() {
        binding.btnSelectExpertise.setOnClickListener(this)
        binding.btnAreasSpecialization.setOnClickListener(this)
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
            binding.btnSelectExpertise.id -> {
               showDropDown(binding.btnSelectExpertise)
            }

            binding.btnAreasSpecialization.id -> {
                showDropDown(binding.btnAreasSpecialization)
            }

            binding.llUploadFile.id -> {
                FilePickerUtils.pickFileFromFileManager(this)
            }
            binding.txtRemove.id -> {
                binding.clFileLayout.visibility = View.GONE
            }
        }
    }

    private fun showDropDown(button: Button) {
        val items = arrayListOf<SearchAbleList>(
            SearchAbleList(0, "Items 0"),
            SearchAbleList(1, "Items 1"),
            SearchAbleList(2, "Items 2"),
            SearchAbleList(3, "Items 3"),
        )
        val dialog = SingleSelectBottomSheetDialogFragment(
            this,
            items,
            getString(R.string.areas_of_specialization),
            0,
            true
        ) { selectedValue ->
            button.text = selectedValue.title
        }
        dialog.show(supportFragmentManager, "SingleSelectBottomSheetDialogFragment")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FilePickerUtils.PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            val fileName = getFileNameFromUri(fileUri!!, this)
            val path = FilePickerUtils.getFullPathFromUri(fileUri, this)
            Log.d("TAG", "onActivityResult: ${fileUri.path}")
            Log.d("TAG", "onActivityResult: $path")
            binding.clFileLayout.visibility = View.VISIBLE
            binding.txtFileName.text = fileName
            // Process the file as needed
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        FilePickerUtils.handlePermissionRequestResult(requestCode, grantResults, this)
    }

}