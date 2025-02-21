package com.wutsi.koki.invoicing.dto

data class InvoiceItem(
    val id: Long = -1,
    val productId: Long = -1,
    val unitPriceId: Long = -1,
    val description: String = "",
    val quantity: Int = -1,
    val unitPrice: Double = 0.0,
    val subTotal: Double = 0.0,
)
