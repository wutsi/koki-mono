package com.wutsi.koki.webscraping.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class CreateWebsiteRequest(
    val userId: Long = -1,

    @get:NotBlank
    val baseUrl: String = "",

    @get:NotEmpty
    val listingUrlPrefixes: List<String> = emptyList(),

    val homeUrls: List<String> = emptyList(),
    val contentSelector: String? = null,
    val imageSelector: String? = null,
    val active: Boolean = true,
)
