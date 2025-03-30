package com.wutsi.koki.payment.dto

import java.util.Date

data class PaymentMethodInterac(
    val id: String = "",
    val referenceNumber: String = "",
    val bankName: String = "",
    val sentAt: Date? = null,
    val clearedAt: Date? = null,
)
