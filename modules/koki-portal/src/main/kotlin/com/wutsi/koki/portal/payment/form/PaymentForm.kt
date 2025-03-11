package com.wutsi.koki.portal.payment.form

import com.wutsi.koki.payment.dto.PaymentMethodType

data class PaymentForm(
    val invoiceId: Long = -1,
    val paymentMethodType: PaymentMethodType = PaymentMethodType.UNKNOWN,
    val amount: Double = 0.0,
    val currency: String = "",
    val description: String? = null,

    // Cash
    val collectedById: Long? = null,
    val collectedAt: String? = null,

    // Check
    val checkNumber: String = "",
    val bankName: String = "",
    val checkDate: String? = null,
    val clearedAt: String? = null,

    // Interact
    val referenceNumber: String = "",
    // val bankName: String = "",
    val sentAt: String? = null,
    // val clearedAt: String? = null,
)
