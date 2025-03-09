package com.wutsi.koki.payment.dto

import java.util.Date

data class PaymentMethodCash(
    val id: String = "",
    val collectedById: Long? = null,
    val collectedAt: Date? = null,
)
