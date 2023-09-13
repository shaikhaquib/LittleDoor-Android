package com.devative.littledoor.model

data class PostCommentModel(
    val current_page: Int,
    val `data`: List<Data>,
    val last_page: Int,
    val per_page: Int,
    val status: Boolean,
    val total: Int
) {
    data class Data(
        val comment: String,
        val comment_by: String,
        val commented_at: String,
        val commented_user_profile: String?,
        val id: Int
    )
}