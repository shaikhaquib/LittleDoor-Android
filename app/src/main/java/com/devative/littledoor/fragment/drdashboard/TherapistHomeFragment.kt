package com.devative.littledoor.fragment.drdashboard

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.devative.littledoor.ChatUi.liveStreaming.LiveStreaming
import com.devative.littledoor.R
import com.devative.littledoor.activity.MainActivity
import com.devative.littledoor.activity.RevenueActivity
import com.devative.littledoor.activity.THAddSessionDetailsActivity
import com.devative.littledoor.adapter.AppointmentAdapter
import com.devative.littledoor.adapter.SliderAdapter
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.architecturalComponents.helper.Status
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.architecturalComponents.viewmodel.TransactionViewModel
import com.devative.littledoor.databinding.TherapistHomeFragmentBinding
import com.devative.littledoor.model.DRStatsModel
import com.devative.littledoor.model.SearchAbleList
import com.devative.littledoor.model.SliderModel
import com.devative.littledoor.model.UserAppointmentModel
import com.devative.littledoor.model.UserAppointmentModel.*
import com.devative.littledoor.model.UserDetails
import com.devative.littledoor.util.Logger
import com.devative.littledoor.util.Progress
import com.devative.littledoor.util.SingleSelectBottomSheetDialogFragment
import com.devative.littledoor.util.Utility
import com.devative.littledoor.util.Utility.isCurrentDate
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Created by AQUIB RASHID SHAIKH on 20-03-2023.
 */
@AndroidEntryPoint
class TherapistHomeFragment : Fragment() {
    private var basicDetails: UserDetails.Data? = null
    private lateinit var binding: TherapistHomeFragmentBinding
    private val vm: MainViewModel by activityViewModels()
    private val trViewModel: TransactionViewModel by activityViewModels()
    private val mainList = ArrayList<Data>()
    private var statList: DRStatsModel? = null
    private val list = ArrayList<Data>()
    lateinit var adapter: AppointmentAdapter
    private var filterCode = 1
    private lateinit var sliderAdapter: SliderAdapter
    private val sliderItems = ArrayList<SliderModel.Data>()
    private lateinit var autoScrollJob: Job
    val progress: Progress by lazy {
        Progress(requireActivity() as AppCompatActivity)
    }

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
        vm.fetchUserData()
        vm.basicDetails.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                basicDetails = it[0]
                updateUI()
            }
        }

        setUpList()
    }

    private fun setUpList() {

        binding.txtViewAll.setOnClickListener {
            (requireActivity() as MainActivity).setNavigationSelection(R.id.bottom_navigation_calender)
        }
        binding.btnSetupSession.setOnClickListener {
            startActivity(Intent(requireActivity(),THAddSessionDetailsActivity::class.java))
        }

        binding.txtFilterName.setOnClickListener {
            val items = arrayListOf<SearchAbleList>(
                SearchAbleList(0, "Weekly"),
                SearchAbleList(1, "Last 15 Days"),
                SearchAbleList(2, "Monthly"),
            )
            val selectedValue = 0
            val dialog = SingleSelectBottomSheetDialogFragment(
                requireActivity(),
                items,
                "Select an option",
                selectedValue
            ) { selected ->
                Logger.d("TAG", "onCreate: $selected")
                binding.txtFilterName.text = selected.title
                setUpLineChart()
            }
            dialog.show(
                requireActivity().supportFragmentManager,
                "SingleSelectBottomSheetDialogFragment"
            )
        }
        binding.txtRevenue.setOnClickListener {
            startActivity(Intent(requireActivity(), RevenueActivity::class.java))
        }
        observe()
        adapter = AppointmentAdapter(requireActivity(), list, object :
            AppointmentAdapter.AppointmentAdapterEvent {
            override fun onclick(position: Int) {

            }
        }, 2)
        adapter.setHasStableIds(true)
        binding.rvAppointment.adapter = adapter
    }

    private fun observe() {
        vm.getUserBookedAppointment()
        vm.getPromotions()
        vm.getChatList()
        vm.getDoctorStats()
        trViewModel.getRevenue()
        vm.getDoctorStats.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    if (it.data?.status == true) {
                        statList = it.data
                        setUpLineChart()
                    }
                }

                Status.ERROR -> {
                }

            }
        }
        vm.getUserBookedAppointment.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    if (!progress.isShowing())
                        progress.show()
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        mainList.clear()
                        if (it.data.data.isNotEmpty()) {
                            mainList.addAll(it.data.data as ArrayList)
                            refreshList()
                        }
                    } else {
                        binding.liJoinSession.visibility = View.GONE
                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
                    binding.liJoinSession.visibility = View.GONE
                }

            }
        }
        trViewModel.getRevenue.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        binding.txtRevenue.text = it.data.data.total_trasaction_amount.toString()
                    }
                }

                Status.ERROR -> {
                }

            }
        }
        vm.getChat.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {

                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        binding.txtPatientCount.text = it.data.data?.size.toString() ?: "0"
                    }
                }

                Status.ERROR -> {
                }

            }
        }
        vm.promotions.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    progress.dismiss()
                    if (it.data?.status == true) {
                        sliderItems.clear()
                        if (it.data.data.isNotEmpty()) {
                            sliderItems.addAll(it.data.data as ArrayList)
                            handlePromotionAdapter()
                        }
                    } else {
                        Utility.errorToast(
                            requireContext(),
                            getString(R.string.some_thing_went_wrong)
                        )
                    }
                }

                Status.ERROR -> {
                    progress.dismiss()
                    it.message?.let { it1 ->
                        Utility.errorToast(
                            requireContext(),
                            getString(R.string.some_thing_went_wrong)
                        )
                    }
                }

            }
        }
    }

    private fun handlePromotionAdapter() {
        sliderAdapter = SliderAdapter(sliderItems)
        binding.viewPager.adapter = sliderAdapter

        autoScrollJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(3000)
                withContext(Dispatchers.Main) {
                    binding.viewPager.currentItem =
                        (binding.viewPager.currentItem + 1) % sliderItems.size
                }
            }
        }
    }

    private fun updateUI() {
        binding.txtUserName.text = basicDetails?.name

        if (basicDetails?.status  != 1){
            binding.bannerAccountNotActive.visibility = View.VISIBLE
            binding.txtRevenue.isEnabled = false
        }else{
            binding.bannerAccountNotActive.visibility = View.GONE
            binding.txtRevenue.isEnabled = true
        }

        basicDetails?.image_url?.let { it1 ->
            binding.imgProfile.load(
                it1,
                R.drawable.profile_view
            )
        }
        binding.imgProfile.borderColor =
            ContextCompat.getColor(requireContext(), R.color.grey_primary)
        binding.imgProfile.borderWidth = 2
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
        statList?.let {

            if (binding.txtFilterName.text.toString() == "Weekly" && it.data.last_seven_day_wise != null) {
                for ((index, data) in it.data.last_seven_day_wise.withIndex()) {
                    sales.add(Entry(index.toFloat(), data.appointment_count.toFloat()))
                }
            }
            if (binding.txtFilterName.text.toString() == "Last 15 Days" && it.data.last_fifteen_day_wise != null) {
                for ((index, data) in it.data.last_fifteen_day_wise.withIndex()) {
                    sales.add(Entry(index.toFloat(), data.appointment_count.toFloat()))
                }
            }
            if (binding.txtFilterName.text.toString() == "Monthly" && it.data.month_wise != null) {
                for ((index, data) in it.data.month_wise.withIndex()) {
                    sales.add(Entry(index.toFloat(), data.appointment_count.toFloat()))
                }
            }
        }
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
            ContextCompat.getColor(requireContext(), R.color.primary_trans),
            ContextCompat.getColor(requireContext(), R.color.white_for_black),
            Shader.TileMode.CLAMP
        )
        paint.shader = linGrad
    }

    fun refreshList() {
        list.clear()
        val l = Constants.filterAndSortData(mainList, filterCode)
        if (l.isNotEmpty()) {
            list.addAll(l)
        }
        adapter.notifyDataSetChanged()
        binding.noAppointmentError.visibility = if (list.isNotEmpty()) View.GONE else View.VISIBLE
        binding.rvAppointment.isVisible = list.isNotEmpty()
        todayAppointment(list)
    }

    fun todayAppointment(dataList: java.util.ArrayList<Data>) {
        val currentTime = Calendar.getInstance()
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val todayData = dataList.filter {
            val appointmentDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.apointmnet_date)
            val appointmentTime =
                SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(it.slot_time)

            val appointmentDateTime = Calendar.getInstance()
            appointmentDateTime.time = appointmentTime!!
            appointmentDateTime.set(Calendar.YEAR, today.get(Calendar.YEAR))
            appointmentDateTime.set(Calendar.MONTH, today.get(Calendar.MONTH))
            appointmentDateTime.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH))

            // Set the appointment time
            appointmentDateTime.set(
                Calendar.HOUR_OF_DAY,
                appointmentDateTime.get(Calendar.HOUR_OF_DAY)
            )
            appointmentDateTime.set(Calendar.MINUTE, appointmentDateTime.get(Calendar.MINUTE))
            appointmentDateTime.set(Calendar.SECOND, 0)
            appointmentDateTime.set(Calendar.MILLISECOND, 0)

            // Add 30 minutes to the appointment time
            appointmentDateTime.add(Calendar.MINUTE, 30)

            appointmentDate == today.time && appointmentDateTime.after(currentTime)
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        val upcomingAppointment = todayData.minByOrNull {
            val appointmentDateTime = sdf.parse("${it.apointmnet_date} ${it.slot_time}")
            appointmentDateTime
        }
        if (upcomingAppointment != null) {
            binding.liJoinSession.visibility = View.VISIBLE
            binding.txtTHName.text = "${upcomingAppointment.doctor_name}"
            binding.txtSlotTime.text = "Today at ${upcomingAppointment.slot_time}"

            binding.btnJoinNow.setOnClickListener {
                //(requireActivity() as MainActivity).setNavigationSelection(R.id.bottom_navigation_search)
                startActivity(
                    Intent(context, LiveStreaming::class.java)
                        .putExtra("CHANNEL_ID", upcomingAppointment.id.toString())
                        .putExtra("IS_HOST", true)
                )

            }

        } else {
            binding.liJoinSession.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::autoScrollJob.isInitialized)
            autoScrollJob.cancel()
    }

}