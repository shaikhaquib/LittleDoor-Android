package com.devative.littledoor.util

/**
 * Created by AQUIB RASHID SHAIKH on 23-09-2023.
 */
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.devative.littledoor.databinding.PaymentPromptSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PaymentPromptSheet(
    val event: PaymentPromptEvent? = null
) :
    BottomSheetDialogFragment() {
    private var _binding: PaymentPromptSheetBinding? = null
    private val binding get() = _binding!!
    companion object {
        const val TAG = "PaymentPromptSheet"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PaymentPromptSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSubmit.setOnClickListener{
            event?.onPayment()
            dismiss()
        }
        binding.ivBottomSheetClose.setOnClickListener{
            event?.onDismiss()
            dismiss()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    interface PaymentPromptEvent {
        fun onDismiss()
        fun onPayment()
    }

}

fun showPaymentPrompt(
    fragmentManager: FragmentManager,
    event: PaymentPromptSheet.PaymentPromptEvent
) {
    val bottomSheet = PaymentPromptSheet(event)
    bottomSheet.show(fragmentManager, PaymentPromptSheet.TAG)
}

