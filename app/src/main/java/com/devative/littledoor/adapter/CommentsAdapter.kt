package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.databinding.ItemExploreBinding
import com.devative.littledoor.model.PostCommentModel
import com.devative.littledoor.model.PostModel


class CommentsAdapter(
    val context: Context,
    val list: ArrayList<PostCommentModel.Data>,
) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemExploreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            val item = list[position]
            binding.txtName.text = item.comment_by
            binding.txtDesc.text = item.comment
            binding.txtDate.text = "${Constants.convertDateFormat(item.commented_at,"dd MMM, hh:mm","yyyy-MM-dd hh:mm:ss")}"
            if (!item.commented_user_profile.isNullOrEmpty()){
                binding.imgProfile.load(item.commented_user_profile!!, R.drawable.profile_placeholder)
            }else{
               binding.imgProfile.setImageResource(R.drawable.profile_placeholder)
            }

            binding.chkLike.visibility = View.GONE
            binding.imgChat.visibility = View.GONE
            binding.imgShare.visibility = View.GONE
            binding.imgPost.visibility = View.GONE
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemExploreBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentsAdapter.ViewHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}