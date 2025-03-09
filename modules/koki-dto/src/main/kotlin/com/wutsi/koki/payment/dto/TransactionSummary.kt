package com.wutsi.koki.payment.dto

import java.util.Date

data class TransactionSummary(
    val id: String = "",
    val createById: Long = -1,
    val type: TransactionType = TransactionType.UNKNOWN,
    val method: PaymentMethodType = PaymentMethodType.UNKNOWN,
    val status: TransactionStatus = TransactionStatus.UNKNOWN,
    val amount: Double = 0.0,
    val currency: String = "",
    val errorCode: String? = null,
    val createdAt: Date = Date(),
)
