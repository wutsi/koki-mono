package com.wutsi.koki.tax.dto

import jakarta.validation.constraints.Min

data class UpdateTaxProductRequest(
    val unitPriceId: Long = -1,
    @get:Min(1) val quantity: Int = 1,
    val description: String? = null,
)
