package com.devative.littledoor.ChatUi
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.R
import com.devative.littledoor.databinding.ItemMessageBinding

class MessageAdapter(private val currentUserId: String) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private val messages: MutableList<ChatActivity.Message> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMessageBinding.inflate(inflater, parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun addMessage(message: ChatActivity.Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatActivity.Message) {
            binding.tvMessageText.text = message.messageText
            if (message.userName == currentUserId) {
                // Message sent by the current user
                binding.tvMessageText.setBackgroundResource(R.drawable.bg_message_sent)
                binding.tvMessageText.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                val params = binding.tvMessageText.layoutParams as ViewGroup.MarginLayoutParams
                params.marginStart = 100
                params.marginEnd = 20
                binding.tvMessageText.layoutParams = params
                binding.tvMessageText.layoutParams = params
                val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.gravity = Gravity.END
            } else {
                // Message sent by other users
                binding.tvMessageText.setBackgroundResource(R.drawable.bg_message_received)
                binding.tvMessageText.setTextColor(ContextCompat.getColor(binding.root.context, R.color.neutral_white))
                val params = binding.tvMessageText.layoutParams as ViewGroup.MarginLayoutParams
                params.marginStart = 20
                params.marginEnd = 100
                binding.tvMessageText.layoutParams = params
            }
        }
    }
}
