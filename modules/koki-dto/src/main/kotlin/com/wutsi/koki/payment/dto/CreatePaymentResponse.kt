package com.wutsi.koki.payment.dto

data class CreatePaymentResponse(
    val transactionId: String = "",
    val status: TransactionStatus = TransactionStatus.UNKNOWN,
)
