package com.wutsi.koki.payment.dto

import java.util.Date

data class Transaction(
    val id: String = "",
    val invoiceId: Long = -1,
    val createdById: Long? = null,
    val type: TransactionType = TransactionType.UNKNOWN,
    val paymentMethodType: PaymentMethodType = PaymentMethodType.UNKNOWN,
    val status: TransactionStatus = TransactionStatus.UNKNOWN,
    val gateway: PaymentGateway = PaymentGateway.UNKNOWN,
    val amount: Double = 0.0,
    val currency: String = "",
    val errorCode: String? = null,
    val supplierTransactionId: String? = null,
    val supplierStatus: String? = null,
    val supplierErrorCode: String? = null,
    val supplierErrorMessage: String? = null,
    val description: String? = null,
    val createdAt: Date = Date(),
    val paymentMethod: PaymentMethod = PaymentMethod(),
)
