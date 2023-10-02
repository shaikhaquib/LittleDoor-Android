package com.devative.littledoor.util
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.devative.littledoor.databinding.ViewNumericKeyboardBinding

/**
 * Created by AQUIB RASHID SHAIKH on 21-09-2023.
 */

class NumericKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var onTypeListener: OnTypeListener? = null
    private var onSubmitListener: OnSubmitListener? = null

    private val binding: ViewNumericKeyboardBinding =
        ViewNumericKeyboardBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        setupButtons()
    }

    private fun setupButtons() {
        binding.button0.setOnClickListener(this)
        binding.button1.setOnClickListener(this)
        binding.button2.setOnClickListener(this)
        binding.button3.setOnClickListener(this)
        binding.button4.setOnClickListener(this)
        binding.button5.setOnClickListener(this)
        binding.button6.setOnClickListener(this)
        binding.button7.setOnClickListener(this)
        binding.button8.setOnClickListener(this)
        binding.button9.setOnClickListener(this)
        binding.buttonBackspace.setOnClickListener(this)
        binding.buttonDone.setOnClickListener(this)
    }

    override fun onClick(view: View) {

        when (view.id) {
            binding.buttonBackspace.id -> {
                onTypeListener?.onBackSpace()
            }
            binding.buttonDone.id -> {
                onSubmitListener?.onSubmit()
            }
            else -> {
                val button = view as Button
                val buttonText = button.text.toString()
                onTypeListener?.onType(buttonText)
            }
        }
    }

    fun setOnTypeListener(listener: OnTypeListener) {
        onTypeListener = listener
    }

    fun setOnSubmitListener(listener: OnSubmitListener) {
        onSubmitListener = listener
    }

    interface OnTypeListener {
        fun onType(text: String)
        fun onBackSpace()
    }

    interface OnSubmitListener {
        fun onSubmit()
    }
}