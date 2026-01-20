package com.wutsi.koki.listing.server.domain

import java.util.Date

data class Feed(
    val id: Long = -1,
    val name: String = "",
    val url: String = "",
    val listingUrlPrefix: String = "",
    val contentSelector: String = "",
    val imageSelector: String? = "",
    val createdAt: Date = Date(),
)
