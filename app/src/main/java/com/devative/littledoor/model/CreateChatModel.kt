package com.devative.littledoor.model

data class CreateChatModel(
    val `data`: List<ChatListResponse.Data>,
    val status: Boolean
)