package com.wutsi.koki.tax.dto

import jakarta.validation.constraints.NotEmpty

data class CreateTaxProductRequest(
    val taxId: Long = -1,
    @get:NotEmpty val offers: List<Offer> = emptyList(),
)
