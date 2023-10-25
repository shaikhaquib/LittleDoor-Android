package com.devative.littledoor.adapter

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.ChatUi.liveStreaming.DigiLiveStartPoint
import com.devative.littledoor.ChatUi.liveStreaming.LiveStreaming
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Constants.hasDatePassed
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.databinding.ItemAppointmentBinding
import com.devative.littledoor.databinding.ItemUserAppointmentBinding
import com.devative.littledoor.model.UserAppointmentModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserAppointmentListAdapter(
    private val context: Context,
    private val list :ArrayList<UserAppointmentModel.Data>,
    private val event: UserAppointmentAdapterEvent,
    private val maxLength: Int = -1,
) : RecyclerView.Adapter<UserAppointmentListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemUserAppointmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            val item = list[position]
            binding.txtName.text = item.doctor_name
            binding.circleImageView.load(item.doctor_profile)
            binding.txtSubText.text = item.doctor_category[0]
            binding.txtTimeRemaining.text = Constants.getTimeRemaining("${item.apointmnet_date}, ${item.slot_time}")
            binding.txtDateTIme.text = "${Constants.convertDateFormat(item.apointmnet_date,"dd MMM")}, ${item.slot_time}"

            if (hasDatePassed(item.apointmnet_date, item.slot_time)){
                binding.btnVideo.setTint(ContextCompat.getColor(context,R.color.secondary))
               // binding.btnVideo.isEnabled = false
                binding.btnVideo.setBackgroundColor(Color.parseColor("#F7F8F8"))
                binding.btnVideoCallStart.visibility = View.GONE
            }
            binding.btnVideo.setOnClickListener{
              //  context.startActivity(Intent(context,DigiLiveStartPoint::class.java))
                    context.startActivity(Intent(context, LiveStreaming::class.java)
                        .putExtra("CHANNEL_ID",item.id.toString())
                        .putExtra("IS_HOST",true))
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemUserAppointmentBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position)
    }


    override fun getItemCount(): Int {
        return if (maxLength == -1)
            list.size
        else
            2
    }

    interface UserAppointmentAdapterEvent {
        fun onclick(position: Int)
    }

}