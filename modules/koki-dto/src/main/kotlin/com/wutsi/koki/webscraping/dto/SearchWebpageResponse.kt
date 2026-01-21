package com.wutsi.koki.webscraping.dto

data class SearchWebpageResponse(
    val webpages: List<WebpageSummary> = emptyList(),
)
