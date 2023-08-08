package com.devative.littledoor.fragment.drdashboard

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.devative.littledoor.R
import com.devative.littledoor.activity.MainActivity
import com.devative.littledoor.adapter.AppointmentAdapter
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.TherapistHomeFragmentBinding
import com.devative.littledoor.model.UserDetails
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Date


/**
 * Created by AQUIB RASHID SHAIKH on 20-03-2023.
 */
@AndroidEntryPoint
class TherapistHomeFragment  : Fragment() {
    private var basicDetails: UserDetails.Data? = null
    private lateinit var binding: TherapistHomeFragmentBinding
    private lateinit var vm: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TherapistHomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dayGreetings()
        vm = MainViewModel.getViewModel(requireActivity())
        vm.fetchUserData()
        vm.basicDetails.observe(viewLifecycleOwner){
            if (!it.isNullOrEmpty()){
                basicDetails = it[0]
                updateUI()
            }
        }
        setUpLineChart()
        setUpList()
    }

    private fun setUpList() {

        binding.txtViewAll.setOnClickListener {
            (requireActivity() as MainActivity).setNavigationSelection(R.id.bottom_navigation_calender)
        }

        binding.rvAppointment.adapter = AppointmentAdapter(requireActivity(),object:
            AppointmentAdapter.AppointmentAdapterEvent {
            override fun onclick(position: Int) {

            }
        },2)
    }

    private fun updateUI() {
        binding.txtUserName.text = basicDetails?.name
    }

    private fun dayGreetings() {
        val date = Date()
        val cal = Calendar.getInstance()
        cal.time = date
        val hour = cal[Calendar.HOUR_OF_DAY]

        //Set greeting

        //Set greeting
        when (hour) {
            in 12..16 -> {
                binding.txtGreet.text = "${getString(R.string.afternoon_greet)},"
            }
            in 17..20 -> {
                binding.txtGreet.text = "${getString(R.string.evening_greet)},"
            }
            in 21..23 -> {
                binding.txtGreet.text = "${getString(R.string.night_greet)},"
            }
            in 0..4 -> {
                binding.txtGreet.text = "${getString(R.string.night_greet)},"
            }
            else -> {
                binding.txtGreet.text = "${getString(R.string.morning_greet)},"
            }
        }
    }

    private fun setUpLineChart() {
        with(binding.lineChart) {
            animateX(1200, Easing.EaseInSine)
            description.isEnabled = false
            xAxis.setDrawGridLines(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisRight.isEnabled = false
            axisRight.setDrawLabels(false)
            axisRight.mAxisRange = 100f
            xAxis.setDrawLabels(false)
            legend.isEnabled = false   // Hide the legend
        }
        setupGradient(binding.lineChart)
        setDataToLineChart()
    }

    private fun week2(): ArrayList<Entry> {
        val sales = ArrayList<Entry>()
        sales.add(Entry(0f, 11f))
        sales.add(Entry(1f, 13f))
        sales.add(Entry(2f, 18f))
        sales.add(Entry(3f, 16f))
        sales.add(Entry(4f, 22f))
        return sales
    }

    private fun setDataToLineChart() {

        val weekTwoSales = LineDataSet(week2(), "Week 2")
        weekTwoSales.lineWidth = 5f
        weekTwoSales.valueTextSize = 15f
        weekTwoSales.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        weekTwoSales.color = ContextCompat.getColor(requireContext(), R.color.primary)
        weekTwoSales.valueTextColor = ContextCompat.getColor(requireContext(), R.color.primary)
        weekTwoSales.setDrawCircles(false)


        val dataSet = ArrayList<ILineDataSet>()
        dataSet.add(weekTwoSales)

        val lineData = LineData(dataSet)
        lineData.setDrawValues(false)
        binding.lineChart.data = lineData
        binding.lineChart.invalidate()
    }

    private fun setupGradient(mChart: LineChart) {
        val paint = mChart.renderer.paintRender
        val height = mChart.height
        val linGrad = LinearGradient(
            800f, 0f, 0f, 0f,
            ContextCompat.getColor(requireContext(),R.color.primary_trans),
            ContextCompat.getColor(requireContext(),R.color.white_for_black),
            Shader.TileMode.CLAMP
        )
        paint.shader = linGrad
    }

}