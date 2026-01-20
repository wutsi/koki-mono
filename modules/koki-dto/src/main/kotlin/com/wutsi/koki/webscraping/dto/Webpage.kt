package com.wutsi.koki.webscraping.dto

import java.util.Date

data class Webpage(
    val id: Long = -1,
    val websiteId: Long = -1,
    val url: String = "",
    val content: String? = null,
    val imageUrls: List<String> = emptyList(),
    val active: Boolean = true,
    val createdAt: Date = Date(),
)
