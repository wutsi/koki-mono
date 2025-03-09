package com.wutsi.koki.payment.dto

data class PaymentMethodCash(
    val collectedById: Long = -1,
    val receiptNumber: String? = null,
)
