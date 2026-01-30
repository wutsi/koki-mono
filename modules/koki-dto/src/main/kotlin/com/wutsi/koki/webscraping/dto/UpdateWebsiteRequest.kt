package com.wutsi.koki.webscraping.dto

import jakarta.validation.constraints.NotEmpty

data class UpdateWebsiteRequest(
    @get:NotEmpty
    val listingUrlPrefixes: List<String> = emptyList(),

    val contentSelector: String? = null,
    val imageSelector: String? = null,
    val active: Boolean = true,
    val homeUrls: List<String> = emptyList(),
)
