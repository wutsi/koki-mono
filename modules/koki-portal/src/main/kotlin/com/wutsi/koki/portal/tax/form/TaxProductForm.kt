package com.wutsi.koki.portal.tax.form

data class TaxProductForm(
    val taxId: Long = -1,
    val productId: Long = -1,
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
)
