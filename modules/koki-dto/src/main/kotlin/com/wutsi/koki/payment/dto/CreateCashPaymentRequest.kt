package com.wutsi.koki.payment.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.util.Date

data class CreateCashPaymentRequest(
    val invoiceId: Long = -1,
    val amount: Double = 0.0,
    @get:NotEmpty val currency: String = "",
    val description: String? = null,

    @get:NotNull val collectedById: Long? = null,
    val collectedAt: Date? = null,
)
