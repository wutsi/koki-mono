package com.wutsi.koki.webscraping.dto

import java.util.Date

data class WebpageSummary(
    val id: Long = -1,
    val websiteId: Long = -1,
    val listingId: Long? = null,
    val url: String = "",
    val imageUrl: String? = null,
    val active: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
)
