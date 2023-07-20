package com.devative.littledoor.activity.drForms

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
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
import com.devative.littledoor.databinding.ActivityLanguageSelectionBinding
import com.devative.littledoor.model.DoctorDetailsResponse
import com.devative.littledoor.util.Logger
import com.devative.littledoor.util.Progress
import com.google.android.material.chip.Chip
import es.dmoral.toasty.Toasty

class ActivityLanguageSelection : BaseActivity() {
    private val itemList = mutableListOf<String>()
    lateinit var binding: ActivityLanguageSelectionBinding
    private val drVM: DrRegViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        progress = Progress(this)
        initIntent()
        observe()
        uiClick()


    }

    private fun uiClick() {
        val autoCompleteList = getLangList()
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, autoCompleteList)
        binding.autoCompleteTextView.setAdapter(adapter)
        binding.autoCompleteTextView.dropDownAnchor = binding.linearLayout4.id
        binding.autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedItem = parent.getItemAtPosition(position).toString()
                createChip(selectedItem)
            }

        binding.addButton.setOnClickListener {
            val enteredItem = binding.autoCompleteTextView.text.toString()
            if (enteredItem.isNotEmpty() && !itemList.contains(enteredItem)) {
                createChip(enteredItem)
                binding.autoCompleteTextView.text.clear()
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

        binding.btnCreate.setOnClickListener {
            if (itemList.isEmpty()) {
                Toasty.error(applicationContext, "Please select at-least one language").show()
            } else {
                val fileMap = HashMap<String, Uri>()
                val dataMap = HashMap<String, String>()
                for (i in itemList.indices) {
                    dataMap["languages[i]"] = itemList[i]
                }
                dataMap["step"] = "5"
                drVM.uploadData(
                    this@ActivityLanguageSelection,
                    fileMap,
                    dataMap
                )
            }
        }
    }

    private fun getLangList(): Array<String> {
        val autoCompleteList = arrayOf(
            "Afrikaans",
            "Albanian",
            "Amharic",
            "Arabic",
            "Armenian",
            "Azerbaijani",
            "Basque",
            "Belarusian",
            "Bengali",
            "Bosnian",
            "Bulgarian",
            "Catalan",
            "Cebuano",
            "Chinese (Simplified)",
            "Chinese (Traditional)",
            "Corsican",
            "Croatian",
            "Czech",
            "Danish",
            "Dutch",
            "English",
            "Esperanto",
            "Estonian",
            "Finnish",
            "French",
            "Frisian",
            "Galician",
            "Georgian",
            "German",
            "Greek",
            "Gujarati",
            "Haitian Creole",
            "Hausa",
            "Hawaiian",
            "Hebrew",
            "Hindi",
            "Hmong",
            "Hungarian",
            "Icelandic",
            "Igbo",
            "Indonesian",
            "Irish",
            "Italian",
            "Japanese",
            "Javanese",
            "Kannada",
            "Kazakh",
            "Khmer",
            "Kinyarwanda",
            "Korean",
            "Kurdish",
            "Kyrgyz",
            "Lao",
            "Latin",
            "Latvian",
            "Lithuanian",
            "Luxembourgish",
            "Macedonian",
            "Malagasy",
            "Malay",
            "Malayalam",
            "Maltese",
            "Maori",
            "Marathi",
            "Mongolian",
            "Myanmar (Burmese)",
            "Nepali",
            "Norwegian",
            "Nyanja (Chichewa)",
            "Odia (Oriya)",
            "Pashto",
            "Persian",
            "Polish",
            "Portuguese (Portugal, Brazil)",
            "Punjabi",
            "Romanian",
            "Russian",
            "Samoan",
            "Scots Gaelic",
            "Serbian",
            "Sesotho",
            "Shona",
            "Sindhi",
            "Sinhala (Sinhalese)",
            "Slovak",
            "Slovenian",
            "Somali",
            "Spanish",
            "Sundanese",
            "Swahili",
            "Swedish",
            "Tagalog (Filipino)",
            "Tajik",
            "Tamil",
            "Tatar",
            "Telugu",
            "Thai",
            "Turkish",
            "Turkmen",
            "Ukrainian",
            "Urdu",
            "Uyghur",
            "Uzbek",
            "Vietnamese",
            "Welsh",
            "Xhosa",
            "Yiddish",
            "Yoruba",
            "Zulu"
            // Add more languages as needed
        )
        return autoCompleteList
    }

    private fun observe() {
        drVM.uploadResponse.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    progress?.show()
                }

                Status.SUCCESS -> {
                    progress?.dismiss()
                    if (it.data?.status == true) {
                        Toasty.success(applicationContext, it.data.message).show()
                        finish()
                    } else {
                        Toasty.error(applicationContext, it.data!!.message).show()
                    }
                }

                Status.ERROR -> {
                    progress?.show()
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

    private fun initIntent() {
        if (intent.hasExtra(Constants.FORM_EDIT_DATA)) {
            if (intent.hasExtra(Constants.FORM_EDIT_DATA)) {
                val data =
                    intent.getSerializableExtra(Constants.FORM_EDIT_DATA) as DoctorDetailsResponse.Data
                for (language in data.languages) {
                    itemList.add(language)
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



