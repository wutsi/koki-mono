package com.wutsi.koki.payment.dto

import jakarta.validation.constraints.NotEmpty

data class CreateCashPaymentRequest(
    val invoiceId: Long? = null,
    val amount: Double = 0.0,
    @get:NotEmpty val currency: String = "",
    val description: String? = null,
    val collectedById: Long? = null,
    val receiptNumber: String? = null,
)
