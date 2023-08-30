package com.devative.littledoor.util

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.devative.littledoor.adapter.TimeSlotAdapter
import com.devative.littledoor.databinding.AllTimeSloteDialogeBinding
import com.devative.littledoor.model.TimeSLotModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/**
 * Created by AQUIB RASHID SHAIKH on 11-03-2023.
 */


class AllTimeSlotSelectDialog(
    private val list:ArrayList<TimeSLotModel.Data>,
    private val onSubmit: AllTimeSlotSelectDialogEvent
) : BottomSheetDialogFragment(),OnClickListener {


    private lateinit var binding:AllTimeSloteDialogeBinding
    private lateinit var adapter: TimeSlotAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AllTimeSloteDialogeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isFitToContents = false
        val layoutParams: ViewGroup.LayoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        isCancelable = true
        binding.btnSubmit.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
        adapter = TimeSlotAdapter(requireActivity(),list)
        binding.rvTimeSlot.adapter = adapter
    }

    fun setSelection(selected: List<Int>){
        adapter.setSelected(selected)
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (requireActivity() as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.btnSubmit.id -> {
                onSubmit.onSubmit(adapter.getSelected())
                dismiss()
            }
            binding.btnCancel.id -> dismiss()
        }
    }
    interface AllTimeSlotSelectDialogEvent{
        fun onSubmit(selected: List<TimeSLotModel.Data>)
    }
}
