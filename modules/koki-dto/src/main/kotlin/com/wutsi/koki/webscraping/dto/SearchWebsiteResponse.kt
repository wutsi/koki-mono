package com.wutsi.koki.webscraping.dto

data class SearchWebsiteResponse(
    val websites: List<WebsiteSummary> = emptyList()
)
