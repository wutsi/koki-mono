package com.wutsi.koki.payment.dto

data class PaymentMethod(
    val id: Long = -1,
    val cash: PaymentMethodCash? = null,
    val interact: PaymentMethodInteract? = null,
    val check: PaymentMethodCheck? = null,
)
