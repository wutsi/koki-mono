package com.wutsi.koki.portal.product.form

data class PriceForm(
    val productId: Long = -1,
    val name: String? = null,
    val amount: Double = 0.0,
    val currency: String = "",
    val active: Boolean = true,
    val startAt: String? = null,
    val endAt: String? = null,
)
