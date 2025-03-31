package com.wutsi.koki.payment.dto

import java.util.Date

data class PaymentMethodCheck(
    val id: String = "",
    val checkNumber: String = "",
    val bankName: String = "",
    val checkDate: Date? = null,
    val clearedAt: Date? = null,
)
