package com.wutsi.koki.portal.listing.model

import java.util.Date

data class AIListingModel(
    val id: Long = -1,
    val listingId: Long = -1,
    val text: String = "",
    val result: String = "",
    val createdAt: Date = Date(),
)
