package com.devative.littledoor.model

data class SkillResponse(
    val `data`: List<Data>,
    val status: String
) {
    data class Data(
        val id: Int,
        val name: String
    )
}