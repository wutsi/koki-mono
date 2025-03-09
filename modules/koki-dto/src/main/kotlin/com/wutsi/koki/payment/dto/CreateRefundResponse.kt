package com.wutsi.koki.payment.dto

data class CreateRefundResponse(
    val transactionId: String = "",
    val status: TransactionStatus = TransactionStatus.UNKNOWN,
)
