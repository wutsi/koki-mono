package com.wutsi.koki.payment.dto

import jakarta.validation.constraints.NotEmpty

data class CreateRefundRequest(
    @get:NotEmpty val transactionId: String = "",
    val amount: Double = 0.0,
    @get:NotEmpty val currency: String = "",
    val description: String = "",
)
