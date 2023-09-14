package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.databinding.ItemCategoryBinding
import com.devative.littledoor.databinding.ItemTherapistBinding
import com.devative.littledoor.model.CategoryResponse
import com.devative.littledoor.model.DoctotorListRes

class CategoryAdapter(
    val context: Context,
    val dataList:ArrayList<CategoryResponse.Data>,
    val therapistAdapterEvent:CategoryAdapterEvent
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int, data: CategoryResponse.Data) {

            binding.root.setOnClickListener{
                therapistAdapterEvent.onclick(position)
            }

            binding.apply {
                txtName.text = data.name
                data.image_url.let { imgIcon.load(it) }
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position,dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface CategoryAdapterEvent {
        fun onclick(position: Int)
    }
}