package com.wutsi.koki.payment.dto

data class CreateInteractPaymentRequest(
    val invoiceId: Long? = null,
    val transactionId: String = "",
    val senderName: String = "",
    val senderEmail: String = "",
)
