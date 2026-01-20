package com.wutsi.koki.webscraping.dto

import jakarta.validation.constraints.NotBlank

data class UpdateWebsiteRequest(
    @get:NotBlank
    val listingUrlPrefix: String = "",

    val contentSelector: String? = null,
    val imageSelector: String? = null,
    val active: Boolean = true,
)
