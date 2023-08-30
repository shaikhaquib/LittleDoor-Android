package com.devative.littledoor.util

/**
 * Created by AQUIB RASHID SHAIKH on 30-08-2023.
 */
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.devative.littledoor.R
import com.devative.littledoor.databinding.GeneralBottomSheetDialogBinding
import com.devative.littledoor.databinding.ImagePickerSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GeneralBottomSheetDialog : BottomSheetDialogFragment() {

    private var title: String? = null
    private var message: String? = null
    private var negativeButtonText: String? = null
    private var positiveButtonText: String? = null
    private var negativeButtonClickListener: View.OnClickListener? = null
    private var positiveButtonClickListener: View.OnClickListener? = null
    private lateinit var binding: GeneralBottomSheetDialogBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = GeneralBottomSheetDialogBinding.inflate(inflater, container, false)

        binding.dialogTitle.text = title
        binding.dialogMessage.text = message

        if (negativeButtonText != null && negativeButtonClickListener != null) {
            binding.negativeButton.visibility = View.VISIBLE
            binding.negativeButton.text = negativeButtonText
            binding.negativeButton.setOnClickListener(negativeButtonClickListener)
        }

        if (positiveButtonText != null && positiveButtonClickListener != null) {
            binding.positiveButton.visibility = View.VISIBLE
            binding.positiveButton.text = positiveButtonText
            binding.positiveButton.setOnClickListener(positiveButtonClickListener)
        }

        return binding.root
    }
   /* override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(binding.root)
        return dialog
    }*/

    fun setTitle(title: String) {
        this.title = title
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun setNegativeButton(text: String, listener: View.OnClickListener) {
        this.negativeButtonText = text
        this.negativeButtonClickListener = listener
    }

    fun setPositiveButton(text: String, listener: View.OnClickListener) {
        this.positiveButtonText = text
        this.positiveButtonClickListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
    }
}
