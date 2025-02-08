package com.wutsi.koki.product.dto

import java.util.Date

data class PriceSummary(
    val id: Long = -1,
    val productId: Long = -1,
    val accountTypeId: Long? = null,
    val name: String? = null,
    val amount: Double = 0.0,
    val currency: String = "",
    val active: Boolean = true,
    val startAt: Date? = null,
    val endAt: Date? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdById: Long? = null,
    val modifiedById: Long? = null,
)
