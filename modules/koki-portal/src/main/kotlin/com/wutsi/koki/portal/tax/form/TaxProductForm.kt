package com.wutsi.koki.portal.tax.form

data class TaxProductForm(
    val taxId: Long = -1,
    val productId: Long = -1,
    val unitPriceId: Long = -1,
    val quantity: Int = 1,
    val description: String? = null,
)
