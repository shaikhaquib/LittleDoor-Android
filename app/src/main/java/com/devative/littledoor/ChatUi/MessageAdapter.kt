package com.devative.littledoor.ChatUi

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devative.littledoor.R
import com.devative.littledoor.architecturalComponents.helper.Constants.convertTimestampToDate
import com.devative.littledoor.architecturalComponents.helper.Constants.load
import com.devative.littledoor.databinding.ItemReciverMessageBinding
import com.devative.littledoor.databinding.ItemSenderMessageBinding
import com.devative.littledoor.util.FullScreenImageDialogFragment
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.security.AccessController.getContext

class MessageAdapter(private val activity: ChatActivity,private val currentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val messages: MutableList<ChatActivity.Message> = mutableListOf()
    private val sender = 1
    private val receiver = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        if (viewType == sender) {
            val binding = ItemSenderMessageBinding.inflate(inflater, parent, false)
            return SenderViewHolder(binding)
        } else {
            val binding = ItemReciverMessageBinding.inflate(inflater, parent, false)
            return ReceiverViewHolder(binding)

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SenderViewHolder) {
            holder.bind(message)
        } else {
            (holder as ReceiverViewHolder).bind(message)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].userName == currentUserId)
            sender
        else
            receiver
    }

    override fun getItemId(position: Int): Long {
        return messages[position].dateTime
    }

    fun addMessage(message: ChatActivity.Message) {
        if (!messages.contains(message)) {
            messages.add(message)
            notifyItemInserted(messages.size - 1)
        }
    }

    inner class SenderViewHolder(private val binding: ItemSenderMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatActivity.Message) {
            binding.tvMessageText.text = message.messageText
            binding.tvDateTime.text = convertTimestampToDate(message.dateTime, "dd/MM HH:mm")

            binding.tvMessageText.isVisible = !message.messageText.isNullOrEmpty()
            if (message.imageUrl.isNullOrEmpty()) {
                binding.imgPost.visibility = View.GONE
            } else {
                binding.imgPost.visibility = View.VISIBLE
                binding.imgPost.setOnClickListener{
                    openImageDialog(message.imageUrl)
                }
                Log.d("TAG", "bind: $message.imageUrl")
                //    val storageReference = FirebaseStorage.getInstance().reference.child(message.imageUrl)

                Picasso.get()
                    .load(message.imageUrl)
                    .resize(250, 250)
                    .centerCrop()
                    .placeholder(R.drawable.profile_placeholder)
                    .into(binding.imgPost)
            }

            val singleTick = binding.tvDateTime.context.getDrawable(R.drawable.single_tick);
            val doubleTick = binding.tvDateTime.context.getDrawable(R.drawable.double_tick);


            if (message.read) {
                doubleTick?.setBounds(0, 0, 30, 30)
                binding.tvDateTime.setCompoundDrawables(doubleTick, null, null, null)
            } else {
                singleTick?.setBounds(0, 0, 30, 30)
                binding.tvDateTime.setCompoundDrawables(singleTick, null, null, null)
            }
        }
    }

    fun updateMessage(updatedMessage: ChatActivity.Message) {
        val index = messages.indexOfFirst { it.dateTime == updatedMessage.dateTime }
        if (index != -1) {
            messages[index] = updatedMessage
            notifyItemChanged(index)
        }
    }

    inner class ReceiverViewHolder(private val binding: ItemReciverMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatActivity.Message) {
            binding.tvMessageText.text = message.messageText
            binding.tvDateTime.text = convertTimestampToDate(message.dateTime, "dd/MM HH:mm")
            binding.tvMessageText.isVisible = !message.messageText.isNullOrEmpty()

            if (message.imageUrl.isNullOrEmpty()) {
                binding.imgPost.visibility = View.GONE
            } else {
                binding.imgPost.visibility = View.VISIBLE
                binding.imgPost.setOnClickListener{
                    openImageDialog(message.imageUrl)
                }
                Log.d("TAG", "bind: $message.imageUrl")
                Picasso.get()
                    .load(message.imageUrl)
                    .resize(250, 250)
                    .centerCrop()
                    .placeholder(R.drawable.profile_placeholder)
                    .into(binding.imgPost)
            }
        }
    }

    fun openImageDialog(imageUri:String){
        val dialogFragment = FullScreenImageDialogFragment()
        val args = Bundle()
        args.putString("imageUri", imageUri)
        dialogFragment.arguments = args
        dialogFragment.show(activity.supportFragmentManager, "FullScreenImageDialog")
    }

}
