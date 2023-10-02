package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.databinding.ItemBankBinding
import com.devative.littledoor.model.BankDetailsModel

class BankListAdapter(
    private val context: Context,
    private val bankList: ArrayList<BankDetailsModel.Data>,
    private val event: BankListAdapterEvent,
) : RecyclerView.Adapter<BankListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemBankBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            val bankData = bankList[position]
            binding.root.setOnClickListener { event.onclick(position) }
            binding.imgMore.setOnClickListener { event.onclickMenu(position) }
            binding.apply {
                txtName.text = "${bankData.branch_name} ${if(bankData.account_number.length > 4){bankData.account_number.substring(0,3)}else{}}"
                txtSubText.text = bankData.account_type
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemBankBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position)
    }


    override fun getItemCount(): Int {
        return bankList.size
    }

    interface BankListAdapterEvent {
        fun onclick(position: Int)
        fun onclickMenu(position: Int)
    }
}