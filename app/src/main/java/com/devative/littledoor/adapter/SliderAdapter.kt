package com.devative.littledoor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.devative.littledoor.R
import com.devative.littledoor.model.SliderModel

/**
 * Created by AQUIB RASHID SHAIKH on 09-10-2023.
 */
class SliderAdapter(private val sliderItems: List<SliderModel.Data>) :
    RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.slider_item, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val sliderItem = sliderItems[position]
        val requestOptions = RequestOptions().transform(RoundedCorners(16))
        Glide.with(holder.itemView)
            .load(sliderItem.image_url)
            .apply(requestOptions)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return sliderItems.size
    }

    inner class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<AppCompatImageView>(R.id.imageView)
    }

}