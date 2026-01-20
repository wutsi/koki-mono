package com.wutsi.koki.webscraping.dto

data class SearchWebpagesResponse(
    val webpages: List<WebpageSummary> = emptyList(),
)
