package com.wutsi.koki.webscraping.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateWebsiteRequest(
    @get:NotNull
    val userId: Long = -1,

    @get:NotBlank
    val baseUrl: String = "",

    @get:NotBlank
    val listingUrlPrefix: String = "",

    val contentSelector: String? = null,
    val imageSelector: String? = null,
    val active: Boolean = true,
)
