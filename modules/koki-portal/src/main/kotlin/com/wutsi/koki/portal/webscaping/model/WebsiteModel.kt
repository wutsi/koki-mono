package com.wutsi.koki.portal.webscaping.model

import java.util.Date

data class WebsiteModel(
    val id: Long? = null,
    val userId: Long = -1,
    var baseUrl: String = "",
    var listingUrlPrefix: String = "",
    var homeUrls: List<String> = emptyList(),
    var contentSelector: String? = null,
    var imageSelector: String? = null,
    var active: Boolean = true,
    val createdAt: Date = Date(),
)
