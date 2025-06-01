package com.wutsi.koki.invoice.dto

import java.util.Date

data class InvoiceSummary(
    val id: Long = -1,
    val number: Long = -1,
    val orderId: Long? = null,
    val paynowId: String = "",
    val status: InvoiceStatus = InvoiceStatus.UNKNOWN,
    val totalAmount: Double = 0.0,
    val amountPaid: Double = 0.0,
    val amountDue: Double = 0.0,
    val currency: String = "",
    val customer: Customer = Customer(),
    val createdAt: Date = Date(),
    val createdById: Long? = null,
    val modifiedAt: Date = Date(),
    val modifiedById: Long? = null,
    val invoicedAt: Date? = null,
    val dueAt: Date? = null,
)
