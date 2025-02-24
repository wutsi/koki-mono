package com.wutsi.koki.invoice.dto

data class InvoiceSalesTax(
    val id: Long = -1,
    val salesTaxId: Long = -1,
    val rate: Double = 0.0,
    val amount: Double = 0.0,
    val currency: String = "",
)
