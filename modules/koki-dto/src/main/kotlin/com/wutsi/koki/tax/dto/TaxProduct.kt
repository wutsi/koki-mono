package com.wutsi.koki.tax.dto

data class TaxProduct(
    val id: Long = -1,
    val taxId: Long = -1,
    val productId: Long = -1,
    val unitPriceId: Long = -1,
    val description: String? = null,
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val subTotal: Double = 0.0,
    val currency: String = "",
)
