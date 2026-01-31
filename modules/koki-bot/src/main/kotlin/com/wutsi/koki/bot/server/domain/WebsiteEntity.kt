package com.wutsi.koki.bot.server.domain

data class WebsiteEntity(
    val name: String = "",
    var baseUrl: String = "",
    var listingUrlPrefixes: List<String> = emptyList(),
    var homeUrls: List<String> = emptyList(),
    var contentSelector: String? = null,
    var imageSelector: String? = null,
    var active: Boolean = true,
)
