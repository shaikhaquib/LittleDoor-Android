package com.devative.littledoor.activity.drForms

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.devative.littledoor.R
import com.devative.littledoor.databinding.ActivityLanguageSelectionBinding
import com.google.android.material.chip.Chip

class ActivityLanguageSelection : AppCompatActivity() {
    private val itemList = mutableListOf<String>()
    lateinit var binding: ActivityLanguageSelectionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        // Create an ArrayAdapter with the dummy data and set it to the AutoCompleteTextView
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, autoCompleteList)
        binding.autoCompleteTextView.setAdapter(adapter)

        binding.autoCompleteTextView.dropDownAnchor = binding.linearLayout4.id
        // Set an item click listener to the AutoCompleteTextView
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



