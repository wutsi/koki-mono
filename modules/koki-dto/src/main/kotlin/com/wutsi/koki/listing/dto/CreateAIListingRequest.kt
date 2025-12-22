package com.wutsi.koki.listing.dto

import jakarta.validation.constraints.NotEmpty

data class CreateAIListingRequest(
    @get:NotEmpty() val text: String = "",
    val cityId: Long = -1,
)
