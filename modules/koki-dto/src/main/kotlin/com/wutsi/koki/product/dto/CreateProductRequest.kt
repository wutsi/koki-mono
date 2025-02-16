package com.wutsi.koki.product.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class CreateProductRequest(
    val categoryId: Long? = null,
    val type: ProductType = ProductType.UNKNOWN,

    @get:NotEmpty @get:Size(max = 100) val name: String = "",
    @get:Size(max = 30) val code: String? = null,

    val description: String? = null,
    val active: Boolean = true,

    val unitId: Long? = null,
    val quantity: Int? = null,
)
