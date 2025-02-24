package com.wutsi.koki.invoice.dto

data class Item(
    val productId: Long = -1,
    val unitPriceId: Long = -1,
    val unitId: Long? = null,
    val description: String? = null,
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
)
