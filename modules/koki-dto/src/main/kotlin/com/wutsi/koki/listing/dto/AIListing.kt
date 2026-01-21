package com.wutsi.koki.listing.dto

import java.util.Date

data class AIListing(
    val id: Long = -1,
    val listingId: Long = -1,
    val text: String = "",
    val result: String = "",
    val createdAt: Date = Date(),
)
