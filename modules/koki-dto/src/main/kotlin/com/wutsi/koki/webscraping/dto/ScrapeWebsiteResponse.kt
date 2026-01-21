package com.wutsi.koki.webscraping.dto

data class ScrapeWebsiteResponse(
    val webpageImported: Int = 0,
    val webpages: List<Webpage> = emptyList()
)
