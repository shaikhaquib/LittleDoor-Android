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
import androidx.appcompat.app.AppCompatActivity
import com.devative.littledoor.databinding.BottomSheetDailyGenralBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/**
 * Created by AQUIB RASHID SHAIKH on 11-03-2023.
 */


class DailyGeneraleBottomSheet(
    private val context: AppCompatActivity
) : BottomSheetDialogFragment(),OnClickListener {

    private lateinit var binding:BottomSheetDailyGenralBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDailyGenralBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        isCancelable = false
        binding.btnCancel.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }
    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.btnCancel.id -> dismiss()
            binding.btnSubmit.id -> dismiss()
        }

    }

}
