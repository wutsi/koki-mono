package com.wutsi.koki.payment.dto

data class PaymentMethod(
    val cash: PaymentMethodCash? = null,
    val interac: PaymentMethodInterac? = null,
    val check: PaymentMethodCheck? = null,
)
