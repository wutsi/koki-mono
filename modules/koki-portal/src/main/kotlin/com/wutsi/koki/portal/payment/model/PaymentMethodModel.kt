package com.wutsi.koki.portal.payment.model

data class PaymentMethodModel(
    val cash: PaymentMethodCashModel? = null,
    val interac: PaymentMethodInteracModel? = null,
    val check: PaymentMethodCheckModel? = null,
)
