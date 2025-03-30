package com.wutsi.koki.portal.payment.model

import java.util.Date

data class PaymentMethodCheckModel(
    val id: String = "",
    val checkNumber: String = "",
    val bankName: String = "",
    val clearedAt: Date? = null,
    val clearedAtText: String? = null,
)
