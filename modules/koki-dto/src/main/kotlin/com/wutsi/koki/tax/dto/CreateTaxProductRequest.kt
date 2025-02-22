package com.wutsi.koki.tax.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

data class CreateTaxProductRequest(
    val taxId: Long = -1,
    val productId: Long = -1,
    val unitPriceId: Long = -1,
    @get:Min(1) val quantity: Int = 1,
    @get:Size(max = 100) val description: String? = null,
)
