package com.devative.littledoor.ChatUi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devative.littledoor.databinding.ActivityChatBinding
import com.devative.littledoor.databinding.ItemMessageBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var database: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var chatId: String
    private lateinit var userId1: String
    private lateinit var userId2: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Realtime Database
        database = Firebase.database.reference.child("chats")

        // Assign user IDs
        userId1 = "UserA"
        userId2 = "UserB"

        // Generate chat ID
        chatId = generateChatId(userId1, userId2)

        // Set up RecyclerView
        messageAdapter = MessageAdapter(userId1)
        binding.rvMessage.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = messageAdapter
        }

        // Send button click listener
        binding.btnChat.setOnClickListener {
            val messageText = binding.edtMessageBox.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                binding.edtMessageBox.text?.clear()
            }
        }

        // Listen for new messages
        database.child(chatId).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let { messageAdapter.addMessage(it) }
                binding.rvMessage.smoothScrollToPosition(messageAdapter.itemCount - 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendMessage(messageText: String) {
        val timestamp = System.currentTimeMillis()
        val message = Message(
            userName = userId1,
            dateTime = timestamp,
            messageText = messageText
        )
        database.child(chatId).push().setValue(message)
    }

    private fun generateChatId(userId1: String, userId2: String): String {
        val sortedUserIds = listOf(userId1, userId2).sorted()
        return sortedUserIds.joinToString("_")
    }

    data class Message(
        val userName: String = "",
        val dateTime: Long = 0,
        val messageText: String = ""
    )
}
