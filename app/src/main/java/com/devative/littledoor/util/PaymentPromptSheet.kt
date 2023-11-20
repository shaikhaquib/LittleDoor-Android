package com.devative.littledoor.util

/**
 * Created by AQUIB RASHID SHAIKH on 23-09-2023.
 */
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devative.littledoor.databinding.PaymentPromptSheetBinding
import com.devative.littledoor.model.DoctotorListRes
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PaymentPromptSheet(
    val thDetails: DoctotorListRes.Data,
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

        binding.txtPlatform.text = thDetails.platform_charge
        binding.txtGSTandTAX.text = thDetails.tax
        binding.txtTherapistFee.text = thDetails.doctor_session_charge
        binding.txtTotalCharges.text = thDetails.total_amount.toString()

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