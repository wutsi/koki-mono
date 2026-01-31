package com.wutsi.koki.bot.dto

data class ScrapeWebsiteRequest(
    val overwrite: Boolean = false,
    val limit: Int = 20,
)
