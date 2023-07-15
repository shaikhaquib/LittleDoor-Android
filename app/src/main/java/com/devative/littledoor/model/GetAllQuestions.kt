package com.devative.littledoor.model

data class GetAllQuestions(
    val `data`: List<Data>,
    val status: Boolean
):java.io.Serializable {
    data class Data(
        val id: Int,
        val name: String,
        val options: List<Option>
    ):java.io.Serializable {
        data class Option(
            val option: String,
            val option_id: Int
        ):java.io.Serializable
    }
}