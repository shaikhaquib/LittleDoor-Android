package com.devative.littledoor.model

data class PostModel(
    val `data`: List<Data>,
    val status: Boolean
) {

    data class Data(
        val id: Int,
        val post: String,
        val post_by: String,
        val post_image: String?,
        val user_profile_url: String?,
        val is_user_like: Int,
        var post_likes: Int,
        val created_at: String
    )
}