package com.wutsi.koki.webscraping.dto

import java.util.Date

data class Webpage(
    val id: Long = -1,
    val websiteId: Long = -1,
    val listingId: Long? = null,
    val url: String = "",
    val imageUrls: List<String> = emptyList(),
    val content: String? = null,
    val active: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
)
