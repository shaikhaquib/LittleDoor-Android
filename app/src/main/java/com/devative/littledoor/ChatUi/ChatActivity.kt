package com.devative.littledoor.ChatUi

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.devative.littledoor.activity.BaseActivity
import com.devative.littledoor.architecturalComponents.helper.Constants
import com.devative.littledoor.architecturalComponents.viewmodel.MainViewModel
import com.devative.littledoor.databinding.ActivityChatBinding
import com.devative.littledoor.model.ChatListResponse
import com.devative.littledoor.util.Progress
import com.devative.littledoor.util.imageUtil.CompressImage
import com.devative.littledoor.util.imageUtil.FileUtil.from
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nareshchocha.filepickerlibrary.picker.PickerUtils.createFileGetUri
import java.io.File
import java.util.UUID


class ChatActivity : BaseActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var database: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private val mainViewModel: MainViewModel by viewModels()
    private var chatData:ChatListResponse.Data? = null
    private lateinit var storageReference: StorageReference
    private var userId = ""

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
        progress.show()
        if (imageUri != null) {
            // Upload the image to Firebase Storage
          //  val compressedImageFile = compress()
            val insertImage = from(this, imageUri)

            val compressImage = CompressImage(this)
                .setMaxWidth(640)
                .setMaxHeight(480)
                .setQuality(35)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(
                    File(
                        Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM), "").absolutePath)
                .compressToFile(insertImage!!)


            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("images/${UUID.randomUUID()}.${compressImage.name}")
            val uploadTask = imageRef.putFile(createFileGetUri(compressImage)!!)

            uploadTask.continueWithTask { task ->
                progress.dismiss()
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                progress.dismiss()
                if (task.isSuccessful) {
                  imageRef.downloadUrl.addOnSuccessListener {
                      val downloadUri: Uri = it
                      val download_url = task.result
                      sendMessage("", download_url.toString())
                  }
                    // Call the sendMessage function with the image URL
                } else {
                    // Handle the error
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        chatData = intent.getSerializableExtra("data") as ChatListResponse.Data?
        // Initialize Firebase Realtime Database
        database = Firebase.database.reference.child("chats")
        // Initialize Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference.child("chat_images")
        mainViewModel.fetchUserData()
        mainViewModel.basicDetails.observe(this){
            if (!it.isNullOrEmpty()){
                basicDetails = it[0]
              initData()
            }
        }
        // Set up RecyclerView
    }

    private fun initData() {
        if(Constants.isDoctor) {
            userId = basicDetails?.doctor_id.toString()
            binding.toolbar.title = chatData?.patient_name
        } else {
            userId = basicDetails?.pateint_id.toString()
            binding.toolbar.title = chatData?.doctor_name
        }
        messageAdapter = MessageAdapter(this,userId)
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
        database.child(chatData?.chat_id.toString()).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)

                message?.let { messageAdapter.addMessage(it) }
                binding.rvMessage.smoothScrollToPosition(messageAdapter.itemCount - 1)
                // Update the read status of the message
                if (message?.userName != userId) {
                    snapshot.ref.child("read").setValue(true)
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let { messageAdapter.updateMessage(it) }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

        // Image upload button click listener
        binding.btnAttach.setOnClickListener {
            // Open image picker
            pickImageLauncher.launch("image/*")
        }
    }

    private fun sendMessage(messageText: String, imageUrl: String? = null) {
        val timestamp = System.currentTimeMillis()
        val message = Message(
            userName = userId,
            dateTime = timestamp,
            messageText = messageText,
            imageUrl = imageUrl,
            read = false
        )
        database.child(chatData?.chat_id.toString()).push().setValue(message)
    }

    private fun generateChatId(userId1: String, userId2: String): String {
        val sortedUserIds = listOf(userId1, userId2).sorted()
        return sortedUserIds.joinToString("_")
    }

    data class Message(
        val userName: String = "",
        val dateTime: Long = 0,
        val messageText: String = "",
        val imageUrl: String? = null,
        val read: Boolean = false
    )

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
