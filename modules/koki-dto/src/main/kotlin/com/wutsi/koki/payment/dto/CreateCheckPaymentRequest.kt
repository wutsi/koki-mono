package com.wutsi.koki.payment.dto

data class CreateCheckPaymentRequest(
    val invoiceId: Long? = null,
    val checkNumber: Int = -1,
    val payeeName: String = "",
    val checkDate: String = "",
    val bankName: String = "",
    val routingNumber: String = "",
    val accountNumber: String = "",
)
