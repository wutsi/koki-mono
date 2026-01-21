package com.wutsi.koki.webscraping.dto

data class ScrapeWebsiteRequest(
    val testMode: Boolean = true,
    val limit: Int = 20,
)
