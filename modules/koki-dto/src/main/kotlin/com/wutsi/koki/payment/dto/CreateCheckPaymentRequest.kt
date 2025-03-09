package com.wutsi.koki.payment.dto

import jakarta.validation.constraints.NotEmpty
import java.util.Date

data class CreateCheckPaymentRequest(
    val invoiceId: Long = -1,
    val amount: Double = 0.0,
    @get:NotEmpty val currency: String = "",
    val description: String? = null,

    @get:NotEmpty val checkNumber: String = "",
    @get:NotEmpty val bankName: String = "",
    val checkDate: Date? = null,
    val clearedAt: Date? = null,
)
