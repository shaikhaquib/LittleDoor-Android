package com.devative.littledoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.databinding.ItemExploreBinding
import com.devative.littledoor.model.PostModel


class ExplorerAdapter(
    val context: Context,
    val list: ArrayList<PostModel.Data>,
    val event:ExplorerAdapterEvent
) : RecyclerView.Adapter<ExplorerAdapter.ViewHolder>() {
    var isMyPost:Boolean = false
    inner class ViewHolder(val binding: ItemExploreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int) {
            val item = list[position]
            binding.txtName.text = item.post_by
            binding.txtDesc.text = item.post
            binding.txtDate.text = "${Constants.convertDateFormat(item.created_at,"dd MMM, hh:mm","yyyy-MM-dd hh:mm:ss")}"

            binding.chkLike.text = if (item.post_likes > 0) item.post_likes.toString() else ""
            if (!item.user_profile_url.isNullOrEmpty()){
                binding.imgProfile.load(item.user_profile_url!!, R.drawable.user_default_icon)
            }else{
               binding.imgProfile.setImageResource(R.drawable.user_default_icon)
            }
            if (item.post_image.isNullOrEmpty()){
                binding.imgPost.visibility = View.GONE
            }else {
                binding.imgPost.visibility = View.VISIBLE
                binding.imgPost.load(item.post_image)
            }
            binding.chkLike.isChecked = item.is_user_like == 1
            binding.chkLike.setOnClickListener {
                event.onLike(position,binding.chkLike.isChecked,item.id)
                if (binding.chkLike.isChecked){
                    item.post_likes = item.post_likes+1
                }else{
                    item.post_likes = item.post_likes-1
                }
                binding.chkLike.text = if (item.post_likes > 0) item.post_likes.toString() else ""
            }
            binding.imgChat.setOnClickListener {
                event.onComment(position,item.id)
            }
            binding.imgShare.setOnClickListener {
                event.onShare(position,item)
            }
            binding.more.isVisible = isMyPost
            binding.more.setOnClickListener {
                event.onDelete(position,item)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemExploreBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExplorerAdapter.ViewHolder, position: Int) {
        holder.bindData(position)
        if (position == itemCount - 1) {
            onEndReachedListener?.invoke()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private var onEndReachedListener: (() -> Unit)? = null

    fun setOnEndReachedListener(listener: () -> Unit) {
        onEndReachedListener = listener
    }

    fun setIsMyPost(isMyPost: Boolean) {
        this.isMyPost = isMyPost
    }


    interface ExplorerAdapterEvent {
        fun onclick(position: Int)
        fun onLike(position: Int,isLike: Boolean,postID: Int)
        fun onComment(position: Int,postID: Int)
        fun onShare(position: Int, postID: PostModel.Data)
        fun onDelete(position: Int, postID: PostModel.Data){}

    }
}