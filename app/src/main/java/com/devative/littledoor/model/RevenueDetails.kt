package com.devative.littledoor.model

data class RevenueDetails(
    val `data`: Data,
    val status: Boolean
) {
    data class Data(
        val pending_request_amount_data: List<TransactionData>,
        val successfull_transaction_data: List<TransactionData>,
        val total_pending_amount: Int,
        val total_trasaction_amount: Int
    ) {

        data class TransactionData(
            val created_at: String,
            val id: Int,
            val request_amount: Int,
            val status: Int,
            val transaction_number: String
        )
    }
}