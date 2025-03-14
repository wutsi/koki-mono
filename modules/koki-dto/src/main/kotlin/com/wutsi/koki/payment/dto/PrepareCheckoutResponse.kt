package com.wutsi.koki.payment.dto

data class PrepareCheckoutResponse(
    val transactionId: String = "",
    val redirectUrl: String = "",
    val status: TransactionStatus = TransactionStatus.UNKNOWN,
)
