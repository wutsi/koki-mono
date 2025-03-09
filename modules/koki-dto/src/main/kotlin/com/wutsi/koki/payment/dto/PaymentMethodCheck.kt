package com.wutsi.koki.payment.dto

data class PaymentMethodCheck(
    val checkNumber: Int = -1,
    val checkDate: String = "",
    val bankName: String = "",
    val routingNumber: String = "",
    val accountNumber: String = "",
    val payeeName: String = "",
)
