package com.wutsi.koki.payment.dto

data class SearchTransactionResponse(
    val transactions: List<TransactionSummary> = emptyList()
)
