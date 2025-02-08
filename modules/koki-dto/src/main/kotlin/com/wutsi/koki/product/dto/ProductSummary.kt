package com.wutsi.koki.product.dto

import java.util.Date

data class ProductSummary(
    val id: Long = -1,
    val type: ProductType = ProductType.UNKNOWN,
    val name: String = "",
    val code: String? = null,
    val active: Boolean = true,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdById: Long? = null,
    val modifiedById: Long? = null,
)
