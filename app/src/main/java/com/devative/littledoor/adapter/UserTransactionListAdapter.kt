package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants.IS_DOCTOR
import com.devative.littledoor.architecturalComponents.helper.Constants.isDoctor
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.databinding.ItemUserTransactionBinding
import com.devative.littledoor.model.DoctorTransactionRes

class UserTransactionListAdapter(
    private val context: Context,
    private val list: ArrayList<DoctorTransactionRes.Data>,
    private val event: UserTransactionListAdapterEvent,
) : RecyclerView.Adapter<UserTransactionListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemUserTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            val item = list[position]
            binding.txtName.text = item.patient_name
            binding.txtSubText.text = "${item.status} - ${item.transaction_number}"
            binding.txtAmount.text = "Rs ${item.amount}"
            binding.txtDate.text = "${item.created_at.split(" ")[0]}"
            if (isDoctor) {
                binding.circleImageView.load(item.patient_image,R.drawable.user_default_icon)
                binding.txtName.text = item.patient_name
            } else {
                binding.circleImageView.load(item.doctor_image,R.drawable.therapist_default_icon)
                binding.txtName.text = item.doctor_name
            }
            if (item.status.toLowerCase() == "success") {
                binding.imgStatus.setImageResource(R.drawable.green_success_tick)
            } else {
                binding.imgStatus.setImageResource(R.drawable.failed)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemUserTransactionBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    interface UserTransactionListAdapterEvent {
        fun onclick(position: Int)
    }
}