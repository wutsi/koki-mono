package com.wutsi.koki.payment.dto

data class SearchTransactionResponse(
    val transactions: List<Transaction> = emptyList()
)
