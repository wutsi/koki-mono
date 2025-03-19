package com.wutsi.koki.invoice.dto

import com.wutsi.koki.refdata.dto.Address
import java.util.Date

data class Invoice(
    val id: Long = -1,
    val orderId: Long? = null,
    val taxId: Long? = null,
    val number: Long = -1,
    val status: InvoiceStatus = InvoiceStatus.UNKNOWN,
    val description: String? = null,
    val customer: Customer = Customer(),
    val locale: String? = null,
    val subTotalAmount: Double = 0.0,
    val totalTaxAmount: Double = 0.0,
    val totalDiscountAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val amountPaid: Double = 0.0,
    val amountDue: Double = 0.0,
    val currency: String = "",

    val shippingAddress: Address? = Address(),
    val billingAddress: Address? = Address(),
    val items: List<InvoiceItem> = emptyList(),
    val taxes: List<InvoiceSalesTax> = emptyList(),

    val createdAt: Date = Date(),
    val createdById: Long? = null,
    val modifiedAt: Date = Date(),
    val modifiedById: Long? = null,
    val invoicedAt: Date? = null,
    val dueAt: Date? = null,
)
