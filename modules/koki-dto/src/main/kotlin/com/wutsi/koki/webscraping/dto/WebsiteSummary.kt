package com.wutsi.koki.webscraping.dto

import java.util.Date

data class WebsiteSummary(
    val id: Long? = null,
    val userId: Long = -1,
    var baseUrl: String = "",
    var active: Boolean = true,
    val createdAt: Date = Date(),
)
