package com.wutsi.koki.payment.dto

data class PrepareCheckoutResponse(
    val transactionId: String = "",
    val redirectUrl: String? = null,
    val status: TransactionStatus = TransactionStatus.UNKNOWN,
)
