package com.wutsi.koki.payment.dto

data class PrepareCheckoutRequest(
    val invoiceId: Long = -1,
    val paymentMethodType: PaymentMethodType = PaymentMethodType.UNKNOWN,
)
