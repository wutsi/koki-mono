package com.wutsi.koki.tax.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

data class UpdateTaxProductRequest(
    @get:Min(1) val quantity: Int = 1,
    @get:Size(max = 100) val description: String? = null,
    val unitPrice: Double = 0.0,
)
