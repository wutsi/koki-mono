package com.wutsi.koki.payment.dto

data class PaymentMethodInteract(
    val transactionId: String = "",
    val senderName: String = "",
    val senderEmail: String = "",
)
