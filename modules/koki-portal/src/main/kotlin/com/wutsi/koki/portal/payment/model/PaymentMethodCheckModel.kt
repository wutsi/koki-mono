package com.wutsi.koki.portal.payment.model

import java.util.Date

data class PaymentMethodInteracModel(
    val id: String = "",
    val referenceNumber: String = "",
    val bankName: String = "",
    val sentAt: Date? = null,
    val sentAtText: String? = null,
    val clearedAt: Date? = null,
    val clearedAtText: String? = null,
)
