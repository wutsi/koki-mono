package com.wutsi.koki.portal.webscaping.model

import java.util.Date

data class WebpageModel(
    val id: Long = -1,
    val websiteId: Long = -1,
    val listingId: Long? = null,
    val url: String = "",
    val imageUrl: String? = null,
    val imageUrls: List<String> = emptyList(),
    val content: String? = null,
    val active: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
)
