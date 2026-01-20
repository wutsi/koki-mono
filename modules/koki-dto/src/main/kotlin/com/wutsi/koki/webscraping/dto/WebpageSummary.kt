package com.wutsi.koki.webscraping.dto

import java.util.Date

data class WebpageSummary(
    val id: Long = -1,
    val websiteId: Long = -1,
    val url: String = "",
    val active: Boolean = true,
    val createdAt: Date = Date(),
)
