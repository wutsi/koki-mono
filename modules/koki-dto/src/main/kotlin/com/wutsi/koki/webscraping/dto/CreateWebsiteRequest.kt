package com.wutsi.koki.webscraping.dto

import jakarta.validation.constraints.NotBlank

data class CreateWebsiteRequest(
    val userId: Long = -1,

    @get:NotBlank
    val baseUrl: String = "",

    @get:NotBlank
    val listingUrlPrefix: String = "",

    val homeUrls: List<String> = emptyList(),
    val contentSelector: String? = null,
    val imageSelector: String? = null,
    val active: Boolean = true,
)
