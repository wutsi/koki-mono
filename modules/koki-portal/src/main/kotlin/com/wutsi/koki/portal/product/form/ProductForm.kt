package com.wutsi.koki.portal.product.form

import com.wutsi.koki.product.dto.ProductType

data class ProductForm(
    val type: ProductType = ProductType.UNKNOWN,
    val name: String = "",
    val code: String? = null,
    val description: String? = null,
    val active: Boolean = true,
)
