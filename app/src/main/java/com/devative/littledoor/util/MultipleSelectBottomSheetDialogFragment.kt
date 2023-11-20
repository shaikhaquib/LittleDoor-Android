package com.devative.littledoor.util

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Update
import com.devative.littledoor.R
import com.devative.littledoor.databinding.BottomSheetSingleSelectBinding
import com.devative.littledoor.model.SearchAbleList
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Locale

/**
 * Created by AQUIB RASHID SHAIKH on 18-11-2023.
 */
class MultipleSelectBottomSheetDialogFragment(
    private val context: Context,
    private val items: ArrayList<SearchAbleList>,
    private val title: String,
    private val selectedValuesString: String, // Change to ArrayList for multiple selections
    private val searchAble: Boolean = false,
    private val cancelAble: Boolean = false,
    private val listener: ((selectedValues: String) -> Unit)? = null // Change to ArrayList for multiple selections
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSingleSelectBinding? = null
    private val binding get() = _binding!!
    private val selectedValues =
        ArrayList<SearchAbleList>() // Change to ArrayList for multiple selections

    private lateinit var adapter: MultipleSelectAdapter // Change to MultipleSelectAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSingleSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        isCancelable = cancelAble
        binding.title.text = title
        val selectedTitles =
            selectedValuesString.split(", ") // Convert comma-separated string to list of titles
        selectedValues.addAll(items.filter { selectedTitles.contains(it.title) } )// Find SearchAbleList objects with selected titles
        adapter = MultipleSelectAdapter(items) { selectedItems ->
            val selectedValuesString =
                selectedItems.joinToString(", ") { it.title } // Convert selected items to comma-separated string

        }
        //  adapter.setHasStableIds(true)
        // Set up RecyclerView with adapter

        binding.btnSubmit.setOnClickListener{
            val selectedValuesString = selectedValues.joinToString(", ") { it.title } // Convert selected items to comma-separated string
            listener?.invoke(selectedValuesString)
            dismiss()
        }
        binding.ivBottomSheetClose.setOnClickListener { dismiss() }

        if (searchAble) {
            binding.edtSearch.visibility = View.VISIBLE
        } else {
            binding.edtSearch.visibility = View.GONE
        }
        binding.itemList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.itemList.adapter = adapter

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter(binding.edtSearch.text.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.ivBottomSheetClose.setOnClickListener {
            dismiss()
        }

        binding.edtSearch.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                bottomSheetBehavior.isFitToContents = false
                val layoutParams: ViewGroup.LayoutParams = bottomSheet.layoutParams
                val windowHeight = getWindowHeight()
                if (layoutParams != null) {
                    layoutParams.height = windowHeight
                }
                bottomSheet.layoutParams = layoutParams
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class MultipleSelectAdapter(
        private val items: ArrayList<SearchAbleList>,
        private val onItemClick: (selectedItems: ArrayList<SearchAbleList>) -> Unit // Change to ArrayList for multiple selections
    ) : RecyclerView.Adapter<MultipleSelectAdapter.ViewHolder>() {

        private var filteredItems: ArrayList<SearchAbleList> = ArrayList()

        init {
            filteredItems = items
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_single_select, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = filteredItems[position]
            holder.text.text = item.title
            holder.itemView.isSelected =
                selectedValues.contains(item) // Check if the item is selected
            holder.itemView.setOnClickListener {
                if (selectedValues.contains(item)) {
                    selectedValues.remove(item) // Deselect the item
                } else {
                    selectedValues.add(item) // Select the item
                }
                //   onItemClick.invoke(getSelectedItems()) // Pass the selected items to the listener
                notifyDataSetChanged()
            }

            if (holder.itemView.isSelected) {
                holder.imgIcon.setImageResource(R.drawable.active_checkbox)
            } else {
                holder.imgIcon.setImageResource(R.drawable.inactive_checkbox)
            }
        }

        override fun getItemCount(): Int {
            return filteredItems.size
        }

        fun filter(query: String) {
            filteredItems = ArrayList()
            if (query.isNotEmpty()) {
                for (item in items) {
                    if (item.title.toLowerCase(Locale.getDefault())
                            .contains(query.toLowerCase(Locale.getDefault()))
                    ) {
                        filteredItems.add(item)
                    }
                }
            } else {
                filteredItems = items
            }
            notifyDataSetChanged()
        }

        private fun getSelectedItems(): ArrayList<SearchAbleList> {
            return selectedValues
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val text = itemView.findViewById<AppCompatTextView>(R.id.itemText)
            val imgIcon = itemView.findViewById<AppCompatImageView>(R.id.imgIcon)
        }
    }
}
