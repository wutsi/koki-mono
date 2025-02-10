package com.wutsi.koki.product.dto

import java.util.Date

data class Product(
    val id: Long = -1,
    val type: ProductType = ProductType.UNKNOWN,
    val name: String = "",
    val code: String? = null,
    val description: String? = null,
    val active: Boolean = true,
    val serviceDetails: ServiceDetails? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val createdById: Long? = null,
    val modifiedById: Long? = null,
)
