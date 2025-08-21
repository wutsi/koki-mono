package com.wutsi.koki.refdata.dto

import jakarta.validation.constraints.Size

data class Address(
    @get:Size(max = 255) val street: String? = null,
    @get:Size(max = 30) val postalCode: String? = null,
    @get:Size(max = 2) val country: String? = null,
    val cityId: Long? = null,
    val stateId: Long? = null,
    val neighborhoodId: Long? = null,
)
