package com.wutsi.koki.payment.dto

import java.util.Date

data class TransactionSummary(
    val id: String = "",
    val invoiceId: Long = -1L,
    val createById: Long? = null,
    val type: TransactionType = TransactionType.UNKNOWN,
    val paymentMethodType: PaymentMethodType = PaymentMethodType.UNKNOWN,
    val status: TransactionStatus = TransactionStatus.UNKNOWN,
    val amount: Double = 0.0,
    val currency: String = "",
    val errorCode: String? = null,
    val createdAt: Date = Date(),
)
