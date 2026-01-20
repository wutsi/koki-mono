package com.wutsi.koki.webscraping.dto

data class SearchWebsitesResponse(
    val websites: List<Website> = emptyList(),
)
