package com.devative.littledoor.model

import java.io.Serializable

data class GetAllQuestions(
    val `data`: List<Data>,
    val status: Boolean
):Serializable {
    data class Data(
        val id: Int,
        val options: List<Option>,
        val question_id: Int,
        val question_name: String,
        val sub_category_id: Int,
        val sub_category_name: String
    ):Serializable {
        data class Option(
            val id: Int,
            val option_id: Int,
            val option_name: String,
            val sub_category_question_mapping_id: Int
        ):Serializable
    }
}