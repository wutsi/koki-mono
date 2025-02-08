package com.wutsi.koki.product.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.util.Date

data class UpdatePriceRequest(
    val accountTypeId: Long? = null,

    @get:Size(max = 100) val name: String? = null,
    @get:NotEmpty() @get:Size(max = 3) val currency: String = "",

    val amount: Double = 0.0,
    val active: Boolean = true,
    val startAt: Date? = null,
    val endAt: Date? = null,
)
