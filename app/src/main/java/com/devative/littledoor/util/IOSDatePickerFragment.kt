package com.devative.littledoor.util

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.devative.littledoor.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by AQUIB RASHID SHAIKH on 12-03-2023.
 */



class IOSDatePickerFragment : BottomSheetDialogFragment() {

    private var mMinAge: Int = 18
    private var mDateFormat: String = "yyyy-MM-dd"
    private lateinit var mListener: OnDateSelectedListener

    interface OnDateSelectedListener {
        fun onDateSelected(date: String)
    }

    fun setOnDateSelectedListener(listener: OnDateSelectedListener) {
        mListener = listener
    }

    companion object {
        private const val ARG_MIN_AGE = "min_age"
        private const val ARG_DATE_FORMAT = "date_format"

        fun newInstance(minAge: Int, dateFormat: String): IOSDatePickerFragment {
            val fragment = IOSDatePickerFragment()
            val args = Bundle()
            args.putInt(ARG_MIN_AGE, minAge)
            args.putString(ARG_DATE_FORMAT, dateFormat)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mMinAge = it.getInt(ARG_MIN_AGE, 18)
            mDateFormat = it.getString(ARG_DATE_FORMAT, "yyyy-MM-dd")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ios_date_picker, container, false)
        val datePicker = view.findViewById<DatePicker>(R.id.date_picker)
        val confirmButton = view.findViewById<MaterialButton>(R.id.confirm_button)

     /*   val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)*/

        // Set minimum and maximum dates
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -mMinAge)
        datePicker.maxDate = cal.timeInMillis

        // Set initial date
        datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))

        // Set listener for confirm button
        confirmButton.setOnClickListener {
            val year = datePicker.year
            val month = datePicker.month
            val day = datePicker.dayOfMonth
            val cal = Calendar.getInstance()
            cal.set(year, month, day)

            val sdf = SimpleDateFormat(mDateFormat)
            val date = sdf.format(cal.time)
            mListener.onDateSelected(date)
            dismiss()
        }

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? CoordinatorLayout
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
    }
}
