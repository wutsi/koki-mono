package com.wutsi.koki.invoicing.dto

import java.util.Date

data class Invoice(
    val id: Long = -1,
    val accountId: Long = -1,
    val invoiceNumber: String = "",
    val invoicedAt: Date = Date(),
    val dueAt: Date? = null,
    val subTotalAmount: Double = 0.0,
    val taxAmount: Double = 0.0,
    val discountAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val items: List<InvoiceItem> = emptyList(),
    val taxes: List<InvoiceSalesTax> = emptyList(),
)
