package com.devative.littledoor.model

import java.io.Serializable

data class BankDetailsModel(
    val `data`: List<Data>,
    val status: Boolean
):Serializable {
    data class Data(
        val account_holder_name: String,
        val account_number: String,
        val account_type: String,
        val branch_name: String,
        val documents: String,
        val id: Int,
        val ifsc_code: String
    ):Serializable
}