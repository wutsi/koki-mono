package com.wutsi.koki.portal.product.form

import com.wutsi.koki.product.dto.ProductType

data class ProductForm(
    val type: ProductType = ProductType.UNKNOWN,
    val categoryId: Long? = null,
    val name: String = "",
    val code: String? = null,
    val description: String? = null,
    val active: Boolean = true,
    val unitId: Long? = null,
    val quantity: Int? = null,
    val unitPrice: Double? = null,
    val currency: String = "",
)
